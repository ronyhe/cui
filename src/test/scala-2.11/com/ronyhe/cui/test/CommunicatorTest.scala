package com.ronyhe.cui.test

import java.io.{ByteArrayOutputStream, PrintStream}
import java.util.Scanner

import com.ronyhe.cui.Communicator.Action
import com.ronyhe.cui._
import org.scalactic.{Bad, Good}
import org.scalatest.FunSuite

class CommunicatorTest extends FunSuite {
  import CommunicatorTest.communicatorForTesting

  test("Communicator prints expected output") {
    val prompt = Strings.DefaultPrompt
    val (communicator, out) = communicatorForTesting("random input", prompt)

    val instructionText = "instruction"

    val action = Action(instructionText, s => Good(s))
    communicator.promptFor(action)

    val expected = s"$instructionText\n$prompt"
    val actual = out.toString

    assert(expected === actual)
  }

  test("Communicator receives input correctly") {
    val (communicator, _) = communicatorForTesting("someInput")
    val action = Action("", s => Good(s))
    val actual = communicator.promptFor(action)
    assert("someInput" === actual)
  }

  test("Communicator repeats if parser returns Bad") {
    val firstInput = "first input"
    val secondInput = "second input"
    val (communicator, _) = communicatorForTesting(firstInput + "\n" + secondInput)

    var timesParserWasCalled = 0
    val parser = (s: String) => {
      timesParserWasCalled += 1
      timesParserWasCalled match {
        case 0 | 1 => Bad("")
        case _ => Good(s)
      }
    }

    val action = Action("", parser)
    val result = communicator.promptFor(action)

    assertResult (secondInput) (result)
    assertResult (2) (timesParserWasCalled)
  }

  test("The example test in the scaladoc of the companion's object communicatorForTesting method") {
    val (comm, out) = communicatorForTesting("someInput")
    val action = Actions.text("instruction")
    comm.promptFor(action)
    assert("instruction\n" === out.toString)
  }

}

object CommunicatorTest {
  /** Returns a communicator with controlled input and available output
    *
    * Example:
    * {{{val (comm, out) = communicatorForTesting("someInput")
    * val action = Actions.text("instruction")
    * comm.promptFor(action)
    * assert("instruction\n" === out.toString)}}}
    */
  def communicatorForTesting(input: String, prompt: String = ""): (Communicator with Dsl, ByteArrayOutputStream) = {
    val out = new ByteArrayOutputStream()
    val comm = new Communicator(new Scanner(input), new PrintStream(out), prompt) with Dsl
    (comm, out)
  }
}
