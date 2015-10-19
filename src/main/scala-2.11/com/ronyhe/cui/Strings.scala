package com.ronyhe.cui

/** Provides various strings used throughout the package
  *
  * Important: These strings are intended for interfacing with users.
  * They are not part of the programmatic API and are subject to change.
  */
object Strings {

  val DefaultPrompt = ">>> "
  val MultiChoiceInputSeparator = ","

  val InputsMeaningYes = Set("yes", "y")
  val InputsMeaningNo = Set("no", "n")
  def augmentYesOrNoInstruction(instruction: String) = instruction + "\t(y / n)"


  def augmentMultiChoiceInstruction(instruction: String, options: Seq[String], min: Int, max:Int): String = {
    val sep = Strings.MultiChoiceInputSeparator
    val newLine = "\n"

    val optionsText = options.zipWithIndex.map { t => s"${t._2}) ${t._1}" } .mkString("\n")

    val isSingleChoice = min == max
    val suffix: String = (min, max, isSingleChoice) match {
      case (1, 1, _) => ""
      case (a, b, true) => s"Choose $a options. Separate with $sep. $MultiChoiceInputExample'"
      case (a, b, false) => s"Choose between $a and $b options. $MultiChoiceInputExample'"
    }

    instruction + newLine + optionsText + newLine + "(" + suffix + ")"
  }


  object InputParsingErrorMessages {
    private val Prefix = "\n!!! Error !!!\t"

    val YesOrNo = Prefix + "Type y or n for yes or no respectively and press Enter\n"

    val AmountOfSelectionsIsOutOfBounds = Prefix + "Choose the specified amount of options\n"

    val MultipleSelection = {
      val sep = MultiChoiceInputSeparator
      s"${Prefix}Type whole numbers separated by $sep. $MultiChoiceInputExample\n"
    }

    val SelectionsAreOutOfBounds =
      Prefix + "Selections must be larger or equal to zero and smaller or equal to the amount of options provided\n"
  }

  private val MultiChoiceInputExample = {
    val sep = MultiChoiceInputSeparator
    s"Example: 1${sep}2${sep}3..."
  }

}
