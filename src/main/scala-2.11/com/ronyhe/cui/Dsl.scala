package com.ronyhe.cui

import com.ronyhe.cui.Dsl.Alternatives

/** A trait that enables one to use a communicator through an internal Dsl.
  *
  * This trait is useful for building apps:
  * 
  * {{{val comm = new Communicator(new Scanner(System.in), System.out, ">>> ") with Dsl
  * comm ask "A or B?" suggest new Alternatives[Unit] {
  *   "Option A" will println("You chose option A")
  *   "Option B" will println("You chose option B")
  * } }}}
  *
  *
  * It can also be nested:
  * 
  * {{{val comm = new Communicator(new Scanner(System.in), System.out, ">>> ") with Dsl
  * comm ask "A or B?" suggest new Alternatives[Unit] {
  *   "Option A" will println("You chose option A")
  *   "Option B" will {
  *     comm ask "Option B is risky. Are you sure?" suggest new Alternatives[Unit] {
  *       "Yes, I'm sure" will println("You chose option B")
  *       "No, I immediately regret this decision" will println("You didn't think that one through")
  *     }
  *   }
  * } }}}
  *
  *
  * It can also be used to retrieve values, enabling a more functional style:
  * 
  * {{{val comm = new Communicator(new Scanner(System.in), System.out, ">>> ") with Dsl
  * val howMany = comm ask "How many do you want?" suggest new Alternatives[Int] {
  *   "Only one" returns 1
  *   "Three, please" returns 3
  *   "As many as possible, please" returns {
  *     // Perform some computation
  *     5
  *   }
  * } }}}
  *
  * If you want to retrieve the index of the alternative the user selected, use the 'index' method:
  * {{{
  *   val chosenIndex = comm ask "which one" suggest new Alternatives[Int] {
  *     "This one" returns index
  *     "But this one" returns index
  *   }
  * }}}
  *
  * If the value you need is the text that represents the alternative use the convenience 'text' method:
  * {{{
  *   val chosenString = comm ask "Who?" suggest new Alternatives[String] {
  *     "Alice" returns text
  *     "Bob" returns text
  *   }
   * }}}
  *
  * The functions 'will' and 'returns' are interchangeable. They coexist because one is sometimes more appropriate
  * than the other.
  *
  * Note that the supplied Communicator, [[com.ronyhe.cui.Communicator.BasicCommunicator]], has the Dsl trait
  *
  */
trait Dsl extends Communicator {

  def ask[T](question: String) = new QuestionBuilder[T](question)

  class QuestionBuilder[T](question: String) {
    def suggest(alts: Alternatives[T]) = {
      presentQuestionToUser(question, alts)
    }

    private def presentQuestionToUser(question: String, alts: Alternatives[T]): T = {
      val (strings, funcs) = alts.alternatives.unzip
      val action = Actions.singleChoice(question, strings)
      val selectedIndex = promptFor(action)
      val selectedFunc = funcs(selectedIndex)

      // Bind the missing values before execution
      alts.indexImpl = () => selectedIndex
      alts.textImpl = () => strings(selectedIndex)
      
      selectedFunc()
    }

  }

}

object Dsl {

  /** A trait used to supply a Dsl.QuestionBuilder[T] with the options to display to the user and their effects.
    *
    * The Dsl trait will bind values to 'index' and 'text' just before executing the effect of the option
    * the user selected.
    * This requires client code to avoid using them in contexts that expect them to already be bound. Example:
    * {{{
    *   // Throws IllegalStateException
    *   val wrong = comm ask "Which one?" suggest new Alternatives[String] {
    *     val report = s"The user selected option $index - $text"  // Notice the 'val' keyword
    *     "one" returns report
    *     "two" returns report
    *   }
    *   
    *   // Works as expected
    *   val right = comm ask "Which one?" suggest new Alternatives[String] {
    *     def report = s"The user selected option $index - $text"  // Notice the 'def' keyword
    *     "one" returns report
    *     "two" returns report
    *   }
    * }}}
    */
  trait Alternatives[T] {

    private[Dsl] var alternatives = Seq[(String, () => T)]()
    
    def index: Int = indexImpl()
    def text: String = textImpl()

    // These need to be bound before the execution of an AltFunc.
    // Binding them is done in Dsl.QuestionBuilder[T]#presentQuestionToUser
    private[Dsl] var textImpl: () => String = () => {
      val message = PrematureUsageOfTextOrIndexMessage(true)
      throw new IllegalStateException(message)
    }

    private[Dsl] var indexImpl: () => Int = () => {
      val message = PrematureUsageOfTextOrIndexMessage(false)
      throw new IllegalStateException(message)
    }
    
    class Will(s: String) {
      def will(f: => T)  = {
        alternatives = alternatives :+ (s, () => f)
      }
      def returns(f: => T) = will(f)
    }

    implicit def stringToWill(s: String): Will = new Will(s)

    def PrematureUsageOfTextOrIndexMessage(trueIfTextFalseIfIndex: Boolean) =
      s"Alternatives[T]#${if(trueIfTextFalseIfIndex) "text" else "index"} isn't bound to a value at this point. See " +
        "the scala doc for Dsl.Alternatives[T] for more."
  }

}
