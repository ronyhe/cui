package com.ronyhe.cui.test

import com.ronyhe.cui.Dsl.Alternatives
import org.scalatest.FunSuite

class DslTest extends FunSuite {

  test("Dsl return expected result") {
    val (comm, _) = CommunicatorTest.communicatorForTesting("1")
    val num = comm ask "how many?" suggest new Alternatives[Int] {
      "one" will {1}
      "one" will {2}
    }

    assert(num === 1)
  }

  test("Dsl re-prompts when given invalid input") {
    val (comm, _) = CommunicatorTest.communicatorForTesting("a\n1")

    val num = comm ask "how many?" suggest new Alternatives[Int] {
      "one" will {1}
      "one" will {2}
    }

    assert(num === 1)
  }

  test("Nested Dsl functions correctly with two instances of Communicator") {
    val (outer, _) = CommunicatorTest.communicatorForTesting("1")
    val (inner, _) = CommunicatorTest.communicatorForTesting("2")

    val outerNum = outer ask "first question?" suggest new Alternatives[Int] {
      "outer one" will {
        inner ask "second question?" suggest new Alternatives[Int] {
          "inner one" will 54
          "inner two" will 1000
        }
      }
      "outer two" will {2}
    }

    assert(1000 === outerNum)

  }

  test("Nested Dsl functions correctly with a single instance of Communicator") {
    val (comm, _) = CommunicatorTest.communicatorForTesting("1\n2")

    val outerNum = comm ask "first question?" suggest new Alternatives[Int] {
      "outer one" will {
        comm ask "second question?" suggest new Alternatives[Int] {
          "inner one" will 54
          "inner two" will 1000
        }
      }
      "outer two" will {2}
    }

    assert(1000 === outerNum)
  }

  test("The 'returns' function behaves as expected") {
    val (comm, _) = CommunicatorTest.communicatorForTesting("1")
    val num = comm ask "how many?" suggest new Alternatives[Int] {
      "1" returns 1
      "2" returns 2
    }

    assert(1 === num)
  }

  test("Alternatives with indexes") {
    val alternatives = new Alternatives[Int] {
      "one" returns index
      "two" returns index
    }

    val (commForOne, _) = CommunicatorTest.communicatorForTesting("1")
    val (commForTwo, _) = CommunicatorTest.communicatorForTesting("2")

    val firstNum = commForOne ask "" suggest alternatives
    val secondNum = commForTwo ask "" suggest alternatives

    assert(0 === firstNum)
    assert(1 === secondNum)
  }

  test("Alternatives with text") {
    val alternatives = new Alternatives[String] {
      "one" returns text
      "two" returns text
    }

    val (commForOne, _) = CommunicatorTest.communicatorForTesting("1")
    val (commForTwo, _) = CommunicatorTest.communicatorForTesting("2")

    val first = commForOne ask "" suggest alternatives
    val second = commForTwo ask "" suggest alternatives

    assert("one" === first)
    assert("two" === second)

  }

  test("text and index work correctly when used together") {
    val (comm, _) = CommunicatorTest.communicatorForTesting("1\n2")

    val alternatives = new Alternatives[String] {
      def string = s"You chose option number ${index+1} - $text"
      "one" returns string
      "two" returns string
    }

    val first = comm ask "" suggest alternatives
    val second = comm ask "" suggest alternatives

    assertResult ("You chose option number 1 - one") (first)
    assertResult ("You chose option number 2 - two") (second)
  }

  test("Using or Alternatives.index too early throws IllegalStateException") {
    val (comm, _) = CommunicatorTest.communicatorForTesting("")
      intercept[IllegalStateException] {

      comm ask "Which one?" suggest new Alternatives[String] {
        val report = s"The user selected index $index"  // Notice the 'val' keyword
        "one" returns report
        "two" returns report
      }

    }
  }

  test("Using or Alternatives.text too early throws IllegalStateException") {
    val (comm, _) = CommunicatorTest.communicatorForTesting("")
    intercept[IllegalStateException] {

      comm ask "Which one?" suggest new Alternatives[String] {
        val report = s"The user selected index $text"  // Notice the 'val' keyword
        "one" returns report
        "two" returns report
      }

    }
  }


}
