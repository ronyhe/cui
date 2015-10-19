package com.ronyhe.cui.test

import com.ronyhe.cui.{Strings, UserInputParsers}
import org.scalactic.{Bad, Good}
import org.scalatest.FunSuite

class YesOrNoTest extends FunSuite {

  val parser = UserInputParsers.YesOrNo

  test("Yes or no behaves correctly with normal input") {
    assertResult (Good(true)) (parser("yes"))
    assertResult (Good(true)) (parser("y"))

    assertResult (Good(false)) (parser("no"))
    assertResult (Good(false)) (parser("n"))
  }

  test("Yes or no returns Bad with illegal input") {
    val expectedResult = Bad(Strings.InputParsingErrorMessages.YesOrNo)

    assertResult (expectedResult) (parser(""))
    assertResult (expectedResult) (parser("some meaningless input"))
  }

  test ("Yes or no behaves correctly with case insensitive input") {
    assertResult (Good(true)) (parser("YES"))
    assertResult (Good(true)) (parser("Y"))
    assertResult (Good(true)) (parser("yEs"))
    assertResult (Good(true)) (parser("yES"))
    assertResult (Good(true)) (parser("YeS"))

    assertResult (Good(false)) (parser("No"))
    assertResult (Good(false)) (parser("NO"))
    assertResult (Good(false)) (parser("nO"))
    assertResult (Good(false)) (parser("N"))
  }

  test ("Yes or no behaves correctly with leading or trailing whitespace") {
    assertResult (Good(true)) (parser("yes    "))
    assertResult (Good(true)) (parser("    y"))

    assertResult (Good(false)) (parser("no\n\n"))
    assertResult (Good(false)) (parser("\n\nn"))
    assertResult (Good(false)) (parser("\n\tn\r\n\t"))
  }
}
