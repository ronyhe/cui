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
  *   "Only one" will 1
  *   "Three, please" returns 3
  *   "As many as possible, please" returns {
  *     // Perform some computation
  *     5
  *   }
  * } }}}
  *
  * The functions 'returns' and 'will' are interchangeable. They coexist because one is sometimes more appropriate
  * than the other:
  * {{{
  *   comm ask "What to do?" suggest new Alternatives[Unit] {
  *     "Option A" will println('A')
  *     "Option B" will println('B')
  *   }
  * }}}
  * {{{
  *   val someNumber = comm ask "How much?" suggest new Alternatives {
  *     "Two" returns 2
  *     "Four" returns 4
  *   }
  * }}}
  *
  * Note that the supplied [[com.ronyhe.cui.Communicator.BasicCommunicator]] has this trait
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

    class Will(s: String) {
      def will(f: => T)  = alternatives = alternatives :+ (s, () => f)
      def returns(f: => T) = will(f)
    }

    implicit def stringToWill(s: String): Will = new Will(s)
  }

}
