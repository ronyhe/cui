package com.ronyhe

/** Provides classes and functions for creating a console user interface
  *
  * ==TL;DR==
  * If all you need is a simple UI that communicates with the user through System.in and System.out, you can use
  * the convenience functions provided in this package object. They will all display the expected form of input to the
  * user; and they will all re-prompt if the input is illegal.
  *
  * {{{scala> import com.ronyhe.cui
  *
  * val shouldUseCui = cui.promptForYesOrNo("Should I use cui?")
  * import com.ronyhe.cui
  *
  * scala> Should I use cui?	(y / n)
  * >>> some illegal input
  *
  * !!! Error !!!	Type y or n for yes or no respectively and press Enter
  *
  * Should I use cui?	(y / n)
  * >>> y
  * shouldUseCui: Boolean = true
  * }}}
  *
  * ==Overview==
  * The building blocks are [[com.ronyhe.cui.Communicator]], [[com.ronyhe.cui.Communicator.Action]] and
  * [[com.ronyhe.cui.Communicator.UserInputParser]].
  *
  * These rely heavily on [[org.scalactic.Or]] and [[org.scalactic.ErrorMessage]].
  * The documentation for Or is available at [[http://doc.scalatest.org/2.2.4/index.html#org.scalactic.Or]].
  *  ErrorMessage is simply an alias for a String.
  *
  * Communicator needs a Scanner and a PrintStream for input and output; and a
  * prompt to show to the users when they are expected to type input. For example:
  * {{{val basicCommunicator = new Communicator(new Scanner(System.in), System.out, prompt = ">>> ")}}}
  *
  * The only public method in Communicator is promptFor[T], Its only argument is an Action[T].
  * Action[T] is a case class with two members, an instruction to show to the user and a UserInputParser[T] to convert
  * the
  * received input to a T. It's located in Communicator's companion object.
  *
  * A UserInputParser is simply an alias defined in the Communicator companion object:
  * {{{type UserInputParser[T] = String => T Or ErrorMessage}}}
  *
  * It's meant to receive the text the user typed as a string and return either Good(T) Or Bad(ErrorMessage)
  * If it returns Bad, communicator.promptFor will display the ErrorMessage and re-prompt.
  * If it returns Good, communicator.promptFor will return the result.
  * This means that the method will not return until valid input was delivered.
  *
  * That's the entire structure, which means that all the building blocks are located in
  * [[com.ronyhe.cui.Communicator]].
  * To summarize, all you need is a Communicator and a UserInputParser contained in an action. So to construct your
  * own components from scratch:
  * {{{val communicator = new Communicator(new Scanner(System.in), new PrintStream(System.out), ">>> ")
  * val parser: UserInputParser[String] = {
  *   case "foo" => Bad("No, DO NOT type foo")
  *   case other => Good(other)
  * }
  * val action = Action("Do not type 'foo'", parser)
  * val result = communicator promptFor action
  * }}}
  *
  * ==Ready-made Components==
  * Some basic components are provided out of the box:
  * [[com.ronyhe.cui.Communicator.BasicCommunicator]] is a communicator that uses System.in and System.out with the
  * prompt String ">>> ".
  * The [[com.ronyhe.cui]] package object provides several basic user interactions that use the BasicCommunicator.
  * [[com.ronyhe.cui.UserInputParsers]] has some basic parsers
  * [[com.ronyhe.cui.Actions]] has factory methods that return these basic parsers inside instances of Action. They
  * also add a description of the expected input to the text that's displayed to the user
  *
  * ==Testing==
  * If you'd like to test your application, it's simple to provide controlled data to instances of Communicator:
  * {{{
  * test("example") {
  *   val firstInput = "some input we will reject"
  *   val secondInput = "some other input we will accept"
  *
  *   val scannerThatFeedsInputsOneAfterAnother = new Scanner(firstInput + "\n" + secondInput)
  *   val baosThatWillCaptureOutput = new ByteArrayOutputStream()
  *
  *   val communicator = new Communicator(
  *     in = scannerThatFeedsInputsOneAfterAnother,
  *     out = new PrintStream(baosThatWillCaptureOutput),
  *     prompt = ""
  *   )
  *
  *   val parserThatRejectsTheFirstInput: UserInputParser[String] = {
  *     case `firstInput` => Bad("Not this input, sorry")
  *     case other => Good(other + " is totally cool input")
  *   }
  *   val action = Action("Type something, oh dear user", parserThatRejectsTheFirstInput)
  *
  *   val expectedResult = "some other input we will accept is totally cool input"
  *   val actualResult = communicator promptFor action
  *   assert(expectedResult === actualResult)
  *
  *   val expectedOutput = "Type something, oh dear user\nNot this input, sorry\nType something, oh dear user\n"
  *   val actualOutput = baosThatWillCaptureOutput.toString
  *   assert(expectedOutput === actualOutput)
  * }
  * }}}
  * Note: This test is part of the project's test suite to make sure it passes
  *
  * ==Contact Information==
  * Comments, questions, suggestions and code review are very welcome at: ronyhe@gmail.com
  */
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
    *
    * @throws IllegalArgumentException if options is empty
    * @throws IllegalArgumentException if minAllowed is smaller than zero
    * @throws IllegalArgumentException if maxAllowed is smaller than minAllowed
    * @throws IllegalArgumentException if maxAllowed is larger than the amount of available options
    */
  def promptForMultiChoice(instruction: String, options: Seq[String], minAllowed: Int, maxAllowed: Int): Set[Int] = {
    require(options.nonEmpty)
    require(minAllowed >= 0)
    require(maxAllowed >= minAllowed)
    require(maxAllowed <= options.length)

    val action = Actions.multiChoice(instruction, options, minAllowed, maxAllowed)
    communicator promptFor action
  }

}
