package com.ronyhe

/** Provides classes and functions for creating a console user interface
  *
  * For a brief introduction and tutorial please refer to this project's readme file:
  * [[https://github.com/ronyhe/cui]]
  * */
package object cui {

  private val communicator = Communicator.BasicCommunicator

  /** Display an instruction to the user and return their response */
  def promptForText(instruction: String): String = {
    val action = Actions.text(instruction)
    communicator promptFor action
  }

  /** Display a yes or no question to the user and return their response as a boolean.
    *
    * The text displayed to the user will include instructions regarding the expected form of input.
    * Illegal input will result in displaying an error message and re-prompting.
    *
    * @return true if the user answered yes, false otherwise
    */
  def promptForYesOrNo(question: String): Boolean = {
    val action = Actions.yesOrNo(question)
    communicator promptFor action
  }

  /** Display an instruction and options to the user and return the index of the option the user selected.
    *
    * The options will be displayed to the user beginning at one, but the function will return the actual index, i.e.
    * beginning at zero.
    *
    * Illegal input will result in displaying an error message and re-prompting.
    *
    * @return the index of the option the user selected
    */
  def promptForSingleChoice(instruction: String, options: Seq[String]): Int = {
    val action = Actions.singleChoice(instruction, options)
    communicator promptFor action
  }

  /** Display an instruction and options to the user and return the indexes of the options the user selected.
    *
    * The options will be displayed to the user beginning at one, but the function will return the actual indexes, i.e.
    * beginning at zero.
    *
    * The text displayed to the user will include instructions regarding the expected form of input.
    * Illegal input will result in displaying an error message and re-prompting.
    *
    * @param minAllowed the minimal number of options the user may choose.
    * @param maxAllowed the maximal number of options the user may choose.
    * @return a Set of of the indexes of the options the user selected
    */
  @throws[IllegalArgumentException]("if options is empty")
  @throws[IllegalArgumentException]("if minAllowed is smaller than zero")
  @throws[IllegalArgumentException]("if maxAllowed is smaller than minAllowed")
  @throws[IllegalArgumentException]("if maxAllowed is larger than the amount of available options")
  def promptForMultiChoice(instruction: String, options: Seq[String], minAllowed: Int, maxAllowed: Int): Set[Int] = {
    require(options.nonEmpty)
    require(minAllowed >= 0)
    require(maxAllowed >= minAllowed)
    require(maxAllowed <= options.length)

    val action = Actions.multiChoice(instruction, options, minAllowed, maxAllowed)
    communicator promptFor action
  }

}
