package com.ronyhe.cui

import com.ronyhe.cui.Communicator.{Action, UserInputParser}
import org.scalactic.{Bad, Good}

/** Provides factory methods that produce common-place instances of Action
  * 
  * If relevant, these Actions describe the expected input to the user and re-prompt if the given input is illegal
  */
object Actions {

  /** Returns a basic Action that prompts the user for text */
  def text(instruction: String): Action[String] = Action(instruction, UserInputParsers.Text)

  /** Returns an Action that displays a yes or no question to the user.
    *
    * The Action's parser will return the user's response as boolean. true if the user answered yes, false otherwise.
    *
    * The text displayed to the user will include instructions regarding the expected form of input.
    * Illegal input will result in displaying an error message and re-prompting.
    */
  def yesOrNo(question: String): Action[Boolean] = {
    val augmentedInstruction = Strings.augmentYesOrNoInstruction(question)
    Action(augmentedInstruction, UserInputParsers.YesOrNo)
  }

  /** Returns an Action that displays several options to the user. The user may only choose one option.
    *
    * The indexes of the options will be displayed to the user beginning at one, but the Action's parser will return
    * the usual zero-based index.
    *
    * Illegal input will result in displaying an error message and re-prompting.
    */
  def singleChoice(instruction: String, options: Seq[String]): Action[Int] = {
    require(options.nonEmpty)
    val (min, max) = (1, 1)

    val parserThatReturnsSet = UserInputParsers.multiChoiceParserWithValidation(options.length, min, max)
    val parserThatReturnsInt: UserInputParser[Int] = parserThatReturnsSet(_) match {
      case Good(selection) => Good(selection.head)
      case Bad(message) => Bad(message)
    }

    val augmentedInstruction = Strings.augmentMultiChoiceInstruction(instruction, options, min, max)
    Action(augmentedInstruction, parserThatReturnsInt)
  }

  /** Returns an Action that displays several options to the user. The user may select the amount of options
    * specified by minAllowed and maxAllowed.
    *
    * The indexes of the options will be displayed to the user beginning at one, but the Action's parser will return
    * the usual zero-based indexes.
    *
    * The text displayed to the user will include instructions regarding the expected form of input.
    * Illegal input will result in displaying an error message and re-prompting.
    *
    * @param minAllowed the minimal number of options the user may choose.
    * @param maxAllowed the maximal number of options the user may choose.
    *
    * @throws IllegalArgumentException if: options is empty
    * @throws IllegalArgumentException if  minAllowed is smaller than zero
    * @throws IllegalArgumentException if maxAllowed is smaller than minAllowed
    * @throws IllegalArgumentException if maxAllowed is larger than the amount of available options
    */
  def multiChoice(instruction: String, options: Seq[String],
                  minAllowed: Int, maxAllowed: Int): Action[Set[Int]] = {

    require(options.nonEmpty)
    require(minAllowed >= 0)
    require(maxAllowed >= minAllowed)
    require(maxAllowed <= options.length)

    val text = Strings.augmentMultiChoiceInstruction(instruction, options, minAllowed, maxAllowed)
    val parser = UserInputParsers.multiChoiceParserWithValidation(options.length, minAllowed, maxAllowed)

    Action(text, parser)
  }

}