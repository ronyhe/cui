package com.ronyhe.cui

import java.io.PrintStream
import java.util.Scanner

import com.ronyhe.cui.Communicator.Action
import org.scalactic.{Bad, ErrorMessage, Good, Or}

/** Communicates instances of [[com.ronyhe.cui.Communicator.Action]] to the user.
  *
  * @see [[com.ronyhe.cui.Communicator.BasicCommunicator]] for a basic communicator that uses System.in and
  *     System.out for input and output
  *
  * @param in a Scanner from which to read input
  * @param out a PrintStream with which to display output
  * @param prompt a String to be displayed to users when they're expected to type input
  */
class Communicator(private val in: Scanner, private val out: PrintStream, private val prompt: String) {

  /** Display an Action's instruction to the user and block until the user types valid input
    *
    * This method distinguishes valid from invalid input using the Action's parser return value.
    * If the parser returns Bad(ErrorMessage) the ErrorMessage will be displayed and the user will be re-prompted.
    * Only when the parser returns Good(T) will the method return.
    */
  def promptFor[T](action: Action[T]): T = {
    val input = getUserInput(action.instruction)

    action.parser(input) match {
      case Good(result) => result
      case Bad(message) =>
        out.println(message)
        promptFor(action)
    }
  }

  private def getUserInput(instruction: String): String = {
    out.println(instruction)
    out.print(prompt)
    in.nextLine()
  }

}

object Communicator {

  /** Parses the String the user typed into the console and returns either:
    * Bad(ErrorMessage) in which case the message will be displayed and the user will be re-prompted for input.
    * Good(T) in which case the parser will return T.
    *
    * @see [[com.ronyhe.cui.Communicator.promptFor()]]
    */
  type UserInputParser[T] = String => T Or ErrorMessage

  /** Represents an interaction with the user.
    *
    * @see [[com.ronyhe.cui.Actions]] for factory methods that produce basic instances of Action
    * */
  case class Action[T](instruction: String, parser: UserInputParser[T])

  /** A basic communicator that uses System.in and System.out for input and output.
    *
    * Uses [[com.ronyhe.cui.Strings.DefaultPrompt]] as a prompt String
    */
  val BasicCommunicator = new Communicator(new Scanner(System.in), System.out, Strings.DefaultPrompt)
}
