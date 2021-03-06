# cui - Console User Interface
cui provides easy ways to interact with the user via a text-based console.

## TL;DR
If all you need is a simple UI that communicates with the user through System.in and System.out, you can use
the convenience functions provided in the cui package object. They will all display the expected form of input to the
user; and they will all re-prompt if the input is illegal.
```scala
scala> import cui

val shouldUseCui = cui.promptForYesOrNo("Should I use cui?")
import cui

scala> Should I use cui?	(y / n)
>>> some illegal input

!!! Error !!!	Type y or n for yes or no respectively and press Enter

Should I use cui?	(y / n)
>>> y
shouldUseCui: Boolean = true
```

## Internal DSL
If you're using cui throughout your code, consider using the Dsl trait:
```scala
val comm = cui.Communicator.BasicCommunicator  // Provided out of the box
comm ask "A or B?" suggest new Alternatives[Unit] {
  "Option A" will println("You chose option A")
  "Option B" will println("You chose option B")
}
```
For more on this, refer to the scaladoc of the trait cui.Dsl

## Overview
The building blocks are cui.Communicator, cui.Communicator.Action and
cui.Communicator.UserInputParser.

These rely heavily on org.scalactic.Or and org.scalactic.ErrorMessage.
The documentation for Or is available at http://doc.scalatest.org/2.2.4/index.html#org.scalactic.Or and
 ErrorMessage is simply an alias for a String.

Communicator needs a Scanner and a PrintStream for input and output; and a
prompt to show to the users when they are expected to type input. For example:
```scala
val basicCommunicator = new Communicator(new Scanner(System.in), System.out, prompt = ">>> ")
```

The only public method in Communicator is promptFor[T], its only argument is an Action[T].
Action[T] is a case class with two members, an instruction to show to the user and a UserInputParser[T] to convert
the
received input to a T. It's located in Communicator's companion object.

A UserInputParser is simply an alias defined in the Communicator companion object:
```scala
type UserInputParser[T] = String => T Or ErrorMessage
```
It's meant to receive the text the user typed as a string and return either Good(T) Or Bad(ErrorMessage)
If it returns Bad, communicator.promptFor will display the ErrorMessage and re-prompt.
If it returns Good, communicator.promptFor will return the result.
**This means that the method will not return until valid input will have been provided.**

That's the entire structure, which means that all the building blocks are located in
cui.Communicator.
To summarize, all you need is a Communicator and a UserInputParser contained in an Action. So to construct your
own components from scratch:
```scala
val communicator = new Communicator(new Scanner(System.in), new PrintStream(System.out), ">>> ")
val parser: UserInputParser[String] = {
  case "foo" => Bad("No, DO NOT type foo")
  case other => Good(other)
}
val action = Action("Do not type 'foo'", parser)
val result = communicator promptFor action
```
## Ready-made Components
Some basic components are provided out of the box:
* cui.Communicator.BasicCommunicator is a communicator that uses System.in and System.out with the
prompt String ">>> ".
* The cui package object provides several basic user interactions that use the BasicCommunicator.
* cui.UserInputParsers has some basic parsers
* cui.Actions has factory methods that return these basic parsers inside instances of Action. They
also add a description of the expected input to the text that's displayed to the user

## Testing
If you'd like to test your application, it's simple to provide controlled data to instances of Communicator:
```scala
test("example") {
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
```
Note: This test is part of the project's test suite to make sure it passes

## Build and dependencies
The only dependency cui needs at runtime is the scalactic package: http://www.scalactic.org/
For testing, an additional dependency is required, scalatest: http://www.scalatest.org/
Both dependencies are managed by SBT and declared in the build.sbt file in the project's root directory.

## Acknowledgments
* I'd like to thank Daniel Westheide for aiding me while learning Scala.
  Check out his "Neophyte's Guide to Scala" series at: http://danielwestheide.com/scala/neophytes.html.
  His blog is also a must-read: http://danielwestheide.com/.
* I'd also like to thank fogus (https://github.com/fogus). fogus made an internal Scala DSL which implements BASIC.
  His code helped me a lot while I was trying to learn some of Scala's DSL related features and techniques.
  Check it out at: https://github.com/fogus/baysick

## Contact Information
Comments, questions, suggestions and code review are very welcome at: ronyhe@gmail.com



*This readme file uses the GFM syntax standard: https://help.github.com/articles/github-flavored-markdown/*