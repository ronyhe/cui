package com.ronyhe.cui.test

import com.ronyhe.cui.{Actions, Strings}
import org.scalactic.Bad
import org.scalatest.FunSuite

class ActionsTest extends FunSuite {

  private val SomeText = "Some text"

  test("singleChoice rejects user input with more than one selection") {
    val action = Actions.singleChoice(SomeText, Seq(SomeText, SomeText))
    val parser = action.parser
    val expectedResult = Bad(Strings.InputParsingErrorMessages.AmountOfSelectionsIsOutOfBounds)
    assertResult (expectedResult) (parser("1, 2"))
  }

  def illegalArgument(title: String)(f: => Any): Unit = test(title) {
    intercept[IllegalArgumentException](f)
  }

  illegalArgument("singleChoice rejects empty options list") {
    Actions.singleChoice(SomeText, options = Seq())
  }

  illegalArgument("multiChoice rejects empty option list") {
    Actions.multiChoice(SomeText, options = Seq(), 1, 1)
  }

  illegalArgument("multiChoice rejects negative minAllowed value") {
    Actions.multiChoice(SomeText, Seq(SomeText), minAllowed = -1, 1)
  }

  illegalArgument("multiChoice rejects if minAllowed > maxAllowed") {
    Actions.multiChoice(SomeText, Seq(SomeText), 6, 5)
  }

  illegalArgument("multiChoice rejects if maxAllowed value is larger than the amount of provided options") {
    Actions.multiChoice(SomeText, Seq(SomeText, SomeText), 1, 3)
  }
}
