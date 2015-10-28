package com.ronyhe.cui.test

import java.io.{ByteArrayOutputStream, PrintStream}
import java.util.Scanner

import com.ronyhe.cui.Communicator
import com.ronyhe.cui.Communicator._
import org.scalactic.{Bad, Good}
import org.scalatest.FunSuite

class PackageTest extends FunSuite {
  test("Example test given in the readme file") {
    val firstInput = "some input we will reject"
    val secondInput = "some other input we will accept"

    val scannerThatFeedsInputsOneAfterAnother = new Scanner(firstInput + "\n" + secondInput)
    val baosThatWillCaptureOutput = new ByteArrayOutputStream()

    val communicator = new Communicator(
      in = scannerThatFeedsInputsOneAfterAnother,
      out = new PrintStream(baosThatWillCaptureOutput),
      prompt = ""
    )

    val parserThatRejectsTheFirstInput: UserInputParser[String] = {
      case `firstInput` => Bad("Not this input, sorry")
      case other => Good(other + " is totally cool input")
    }
    val action = Action("Type something, oh dear user", parserThatRejectsTheFirstInput)

    val expectedResult = "some other input we will accept is totally cool input"
    val actualResult = communicator promptFor action
    assert(expectedResult === actualResult)

    val expectedOutput = "Type something, oh dear user\nNot this input, sorry\nType something, oh dear user\n"
    val actualOutput = baosThatWillCaptureOutput.toString
    assert(expectedOutput === actualOutput)
  }

}
