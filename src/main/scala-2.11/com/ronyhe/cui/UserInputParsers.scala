package com.ronyhe.cui

import com.ronyhe.cui.Communicator.UserInputParser
import org.scalactic.{Bad, ErrorMessage, Good, Or}

/** Provides commonplace instances of UserInputParser.
  *
  * If relevant, these parsers reject invalid input and supply an ErrorMessage for the user.
  */
object UserInputParsers {
  import Strings.{InputParsingErrorMessages => ErrorMessages}

  /** A parser for simple text input */
  val Text: UserInputParser[String] = s => Good(s)

  /** A parser for yes or no questions.
    *
    * Returns Good(true) for inputs: "y" and "yes" (case insensitive)
    * Returns Good(false) for inputs: "n" and "no" (case insensitive)
    * Returns Bad(ErrorMessages.YesOrNo) for any other input
    */
  val YesOrNo: UserInputParser[Boolean] = s => {
    val normalized = s.trim.toLowerCase

    val isYes = Strings.InputsMeaningYes contains normalized
    val isNo = Strings.InputsMeaningNo contains normalized

    if (isYes)
      Good(true)
    else if (isNo)
      Good(false)
    else
      Bad(ErrorMessages.YesOrNo)
  }

  /** A parser for multiple choice Actions.
    *
    * The parser expects a comma separated list of integers: "1,2,3".
    * It ignores all whitespace.
    *
    */
  val MultiChoice: UserInputParser[Set[Int]] = input => {
    val separator: String = Strings.MultiChoiceInputSeparator
    val sepAsChar: Char = separator(0)
    
    def parseIntsFromInput(s: String): Set[Int] =
      s.stripMargin(sepAsChar).
      split(separator).
      map(_.trim).
      filterNot(_.isEmpty).
      map(_.toInt - 1).  // Compensate for the fact that the user sees the options' indexes starting at one
      toSet

    try {
      Good(parseIntsFromInput(input))
    } catch {
      case e: Exception => Bad(ErrorMessages.MultipleSelection)
    }
  }

  /** Returns a multi choice parser that validates the selected integers
    *
    * The parser simply wraps MultiChoice and checks that:
    * - All selected integers are between zero(inclusive) and the amount of options(exclusive):
    *   i >= 0 && i < amountOfOptions
    * - The amount of integers selected is between minChoicesAllowed and maxChoicesAllowed (both inclusive):
    *   i >= minChoicesAllowed && i <= maxChoicesAllowed
    */
  def multiChoiceParserWithValidation(amountOfOptions: Int, minChoicesAllowed: Int,
                                      maxChoicesAllowed: Int): UserInputParser[Set[Int]] = s => {

    val unvalidatedSelections: Set[Int] Or ErrorMessage = MultiChoice(s)
    unvalidatedSelections match {
      case Bad(message) => Bad(message)

      case Good(selections) =>
        val allSelectionsAreInBounds = selections forall { i => 0 until amountOfOptions contains i }
        val amountOfSelectionsIsInBounds = minChoicesAllowed to maxChoicesAllowed contains selections.size

        if (!allSelectionsAreInBounds)
          Bad(ErrorMessages.SelectionsAreOutOfBounds)
        else if (!amountOfSelectionsIsInBounds)
          Bad(ErrorMessages.AmountOfSelectionsIsOutOfBounds)
        else
          Good(selections)
    }
  }

}
