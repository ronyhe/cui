package com.ronyhe.cui.test

import com.ronyhe.cui.Strings
import org.scalatest.FunSuite

class StringsTest extends FunSuite {

  test("There are no empty parenthesis at the end of single choice text") {
    val (min, max) = (1, 1)
    val augmentedString = Strings.augmentMultiChoiceInstruction("", Seq(), min ,max)
    assert(!augmentedString.endsWith("()"))
  }

  test("Multi choice options are presented with 1-based indexes") {
    val options = Seq("", "", "")
    val expected = "1) \n2) \n3) "  // Notice the spaces after the parentheses
    val augmentedString = Strings.createOptionsText(options)
    assertResult (expected) (augmentedString)
  }

}
