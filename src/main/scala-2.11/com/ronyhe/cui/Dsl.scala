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
  * Note that the supplied [[com.ronyhe.cui.Communicator.BasicCommunicator]] has the Dsl trait
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
      selectedFunc()
    }

  }

}

object Dsl {
  
  trait Alternatives[T] {
    private[Dsl] var alternatives = Seq[(String, () => T)]()
    var currentIndex = 0

    class Will(s: String) {
      def will(f: => T)  = {
        alternatives = alternatives :+ (s, () => f)
      }
      def returns(f: => T) = will(f)
    }

    def index: Int = {
      currentIndex += 1
      currentIndex - 1
    }

    def text = {
      val i = index
      alternatives(i)._1
    }

    implicit def stringToWill(s: String): Will = new Will(s)
  }

}
