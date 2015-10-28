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

}
