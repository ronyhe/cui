package com.ronyhe.cui.test

import java.io.{ByteArrayOutputStream, PrintStream}
import java.util.Scanner

import com.ronyhe.cui.Communicator.Action
import com.ronyhe.cui._
import org.scalactic.{Bad, Good}
import org.scalatest.FunSuite

class CommunicatorTest extends FunSuite {

  test("Communicator prints expected output") {
    val out = new ByteArrayOutputStream()
    val communicator = new Communicator(new Scanner("random input"), new PrintStream(out), Strings.DefaultPrompt)

    val instructionText = "instruction"

    val action = Action(instructionText, s => Good(s))
    communicator.promptFor(action)

    val expected = s"$instructionText\n${Strings.DefaultPrompt}"
    val actual = out.toString

    assert(expected === actual)
  }

  test("Communicator receives input correctly") {
    val inputText = "someInput"
    val communicator = new Communicator(new Scanner(inputText), new PrintStream(new ByteArrayOutputStream()), "")
    val actual = communicator.promptFor(Action("", s => Good(s)))
    assert(inputText === actual)
  }

  test("Communicator repeats if parser returns Bad") {
    val firstInput = "first input"
    val secondInput = "second input"
    val scannerThatFeedsFirstAndThenSecondInputs = new Scanner(firstInput + "\n" + secondInput)

    val communicator =
      new Communicator(scannerThatFeedsFirstAndThenSecondInputs, new PrintStream(new ByteArrayOutputStream()), "")

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

}
