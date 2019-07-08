package com.github.kkimishima.part1

import cats._
import cats.implicits._

// Monoids and Semigroups
object Chapter2 extends App {
  val mi = Monoid[String].combine("Hi", "there")
  p(mi)
  p(Monoid[String].empty)
  p(Semigroup[String].combine("Hi", "there"))
  p(Monoid[Option[Int]].combine(Some(1), Some(2)))
  p(Monoid[Option[Int]].combine(Some(1), None))
  // 省略文法
  p("Hi" |+| "there")

  val li = List(1, 2, 3)
  p(addAll(li))

  val li2 = List(None, Some(1), Some(2))
  p(addAll(li2))

  def addAll[A: Monoid](list: List[A]): A = {
    val m = implicitly[Monoid[A]]
    list.foldRight(m.empty)(m.combine)
  }

  def p(any: Any): Unit = println(any)
}

