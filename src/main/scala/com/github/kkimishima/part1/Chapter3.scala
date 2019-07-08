package com.github.kkimishima.part1

import cats._
import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, duration}

object Chapter3 extends App {
  val li = List(1, 2, 3)
    .map(_ + 1)
    .map(_.toString)
  p(li)

  // futureのmapもできるよ
  val future: Future[String] =
    Future(123)
      .map(_ + 1)
      .map(_.show)
  Await.ready(future, duration.Duration.Inf)
  future.foreach(p(_))

  val func1: Int => Double = (x: Int) => x.toDouble
  val func2: Double => Double = (y: Double) => y * 2
  p(func1(1))
  //  普通の関数合成
  p(func2(func1(1)))
  // andThenを使用して関数合成
  p((func1 andThen func2) (1))
  // composeを使用して関数合成
  p((func2 compose func1) (1))
  // mapで合成
  p((func1 map func2) (1))

  // 基本の使い方
  val functor1 = Functor[List].map(List(1, 2, 3))(_ * 2)
  p(functor1)
  val functor2 = Functor[Option].map(Some(1))("OK_" + _.toString)
  p(functor2)
  val function3 = Functor[Option].map(None)("OK_" + _.toString)
  p(function3)

  val funcc = (x: Int) => x + 1
  val liftedFunc = Functor[Option].lift(funcc)
  p(liftedFunc(Option(1)))

  //  合成
  val func11 = (a: Int) => a + 1
  val func22 = (a: Int) => a * 2
  val func3 = (a: Int) => a.toString + "!"
  val func4 = func11.map(func22).map(func3)
  p(func4(123))

  // 型コンストラクタを使用
  p(doMath(Option(20)))
  p(doMath(none[Int]))
  p(doMath(List(1, 2, 3)))
  p(doMath(Map("hoge" -> 1)))

  //  p(Functor[Future])
  // functorのカスタム
  implicit val myfunctor: Functor[Option] = new Functor[Option] {
    override def map[A, B](fa: Option[A])(f: A => B): Option[B] = {
      fa.map(f)
    }
  }

  // functorの拡張
  implicit val treeFunctor: Functor[Tree] = new Functor[Tree] {
    override def map[A, B](fa: Tree[A])(f: A => B): Tree[B] = fa match {
      case Branch(left, right) => {
        Branch(map(left)(f), map(right)(f))
      }
      case Leaf(value) => {
        Leaf(f(value))
      }
    }
  }

  p(Tree.leaf("OK").map(_ + "!!!"))
  val leaf1 = Leaf("1")
  val leaf2 = Leaf("2")
  val branch1 = Branch(leaf1, leaf2)
  p(Tree.branch(leaf1, leaf2).map(_ + "!!!"))

  implicit val stringPrintable: Printable[String] = new Printable[String] {
    override def format(value: String): String =
      s""" $value """
  }
  implicit val bootPrintable: Printable[Boolean] = new Printable[Boolean] {
    override def format(value: Boolean): String =
      if (value) "正解!!" else "不正解!!"
  }
  p(format("OK"))


  p(format(true))


  implicit def boxPrintable[A](implicit p: Printable[A]) =
    new Printable[Box[A]] {
      def format(box: Box[A]): String =
        p.format(box.value)
    }

  p(format(Box("Box_OK")))


  implicit val stringCode: Codec[String] = new Codec[String] {
    override def encode(value: String): String = value

    override def decode(value: String): String = value

  }

  implicit val intCodec: Codec[Int] = stringCode.imap(_.toInt, _.toString)
  implicit val booleanCodec: Codec[Boolean] = stringCode.imap(_.toBoolean, _.toString)

  implicit def boxCodec[A](implicit c: Codec[A]): Codec[Box[A]] = c.imap[Box[A]](Box(_), _.value)

  p(encode("OK"))
  p(encode(Box("OK")))

  //  val showString = Show[String]
  //  val showSymbol = Contravariant[Show].
  //    contramap(showString)((sym: Symbol) => s"'${sym.name}")
  //  showSymbol.show('dave)

  def encode[A: Codec](value: A): String = implicitly[Codec[A]].encode(value)

  def decode[A: Codec](value: String): A = implicitly[Codec[A]].decode(value)


  def format[A: Printable](value: A): String = implicitly[Printable[A]].format(value)


  // 型コンストラクターを使用
  def doMath[F[_]](start: F[Int])(implicit functor: Functor[F]): F[Int] = start.map(n => n + 1)

  def p(any: Any): Unit = println(any)
}

trait Printable[A] {
  self =>
  def format(value: A): String

  def contramap[B](f: B => A): Printable[B] = new Printable[B] {
    override def format(value: B): String = self.format(f(value))
  }
}

case class Box[A](value: A)

sealed trait Tree[+A]

case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

case class Leaf[A](value: A) extends Tree[A]

object Tree {
  def branch[A](left: Tree[A], right: Tree[A]): Tree[A] = Branch(left, right)

  def leaf[A](value: A): Tree[A] = Leaf(value)
}

trait Codec[A] {
  def encode(value: A): String

  def decode(value: String): A

  def imap[B](dec: A => B, enc: B => A): Codec[B] = {
    val self = this
    new Codec[B] {
      override def encode(value: B): String = {
        self.encode(enc(value))
      }

      override def decode(value: String): B = {
        dec(self.decode(value))
      }
    }
  }
}

