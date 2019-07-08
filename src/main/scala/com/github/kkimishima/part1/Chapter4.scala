package com.github.kkimishima.part1

import cats._
import cats.implicits._

object Chapter4 extends App {
  p(paresInt("aaaaa"))
  p(paresInt("1"))
  p(divide(1, 2))
  p(divide(1, 0))
  p(stringDivideBy("2", "1"))
  p(stringDivideBy("aaa", "bbb"))
  p(stringDivideBy("1", "2"))

  val fora: List[(Int, Int)] = {
    for {
      x <- List(1, 2, 3)
      y <- List(4, 5, 6)
    } yield (x, y)
  }
  p(fora)

  val demoClassInstances = new MyMonad[DemoClass] {
    override def pure[A](value: A): DemoClass[A] = DemoClass(value)

    override def flatMap[A, B](value: DemoClass[A])(f: A => B): DemoClass[B] = {
      DemoClass(f(value.value))
    }
  }
  p(demoClassInstances.flatMap(DemoClass(1))(_.toString + "!!!!!"))

  val opt1 = Monad[Option].pure(3)
  val opt2 = Monad[Option].flatMap(opt1)(a => Some(a + 2))
  val opt3 = Monad[Option].map(opt2)(a => 100 * a)
  p(opt3)

  val li1 = Monad[List].pure(3)
  val li2 = Monad[List].flatMap(li1)(a => List(a, a * 10))
  val li3 = Monad[List].map(li2)(a => a + 123)
  p(li3)

  p(1.pure[Option])
  p(sumSquare(Option(1), none[Int]))
  p(sumSquare(1: Id[Int], 2: Id[Int]))

  def sumSquare[F[_] : Monad](a: F[Int], b: F[Int]): F[Int] =
    for {
      x <- a
      y <- b
    } yield x * x + y * y

  def stringDivideBy(aStr: String, bStr: String): Option[Int] =
    for {
      aNum <- paresInt(aStr)
      bNum <- paresInt(bStr)
      ans <- divide(aNum, bNum)
    } yield ans

  def paresInt(string: String): Option[Int] =
    scala.util.Try(string.toInt).toOption

  def divide(a: Int, b: Int): Option[Int] =
    if (b == 0) None else Some(a / b)

  def p(any: Any): Unit = println(any)


}

case class DemoClass[A](value: A)

trait MyMonad[F[_]] {
  def pure[A](value: A): F[A]

  def flatMap[A, B](value: F[A])(f: A => B): F[B]
}