package com.enjapan

import scala.collection.Iterator._
import scala.collection.{AbstractIterator, Iterator}

/**
  * Created by Ugo Bataillard on 3/14/16.
  */
package object helpers {
  val envProps = sys.env ++ sys.props

  implicit class RichIterator[A](it:Iterator[A]) {

    /** Takes longest prefix of values produced by this iterator that satisfy a predicate.
      *
      * @param   p The predicate used to test elements.
      * @return An iterator returning the values produced by this iterator, until
      *         this iterator produces a value that does not satisfy
      *         the predicate `p`.
      * @note Reuse: $consumesAndProducesIterator
      */
    def takeUntil(p: A => Boolean): Iterator[A] = new AbstractIterator[A] {
      private var hd: A = _
      private var hdDefined: Boolean = false
      private var tail: Iterator[A] = it

      def hasNext = hdDefined || tail.hasNext && {
        hd = tail.next()
        if (!p(hd)) tail = Iterator.empty
        hdDefined = true
        hdDefined
      }
      def next() = if (hasNext) {hdDefined = false; hd} else empty.next()
    }
  }

}
