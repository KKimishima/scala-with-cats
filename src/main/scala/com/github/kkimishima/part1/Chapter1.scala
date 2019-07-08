package com.github.kkimishima.part1

import java.time.LocalDate
import java.util.Date

import cats._
import cats.implicits._

// Introduction
object Chapter1 extends App {

  import com.github.kkimishima.part1.MyTypeJson.Json
  import com.github.kkimishima.part1.MyTypeJson.JsonSyntax._
  import com.github.kkimishima.part1.MyTypeJson.JsonWriterInstances._

  println(Json.toJson(""))
  val person = Person("ほげさん", "hoge@hogehoge.com")
  println(Json.toJson(person))

  println(person.toJson)
  println(Json.toJson(Option("A")))

  //  catsのshow型クラスを利用
  val showInt: Show[Int] = Show.apply[Int]
  val showString: Show[String] = Show.apply[String]
  // インスタンスを直接利用
  println(showInt.show(1))
  println(showString.show("show"))

  // 拡張関数のシンタックスを利用
  println(1111.show)
  println("ShowSyntax".show)

  //  自分でインスタンスを定義して拡張
  implicit val dateShow: Show[LocalDate] =
    new Show[LocalDate] {
      override def show(t: LocalDate): String =
        s"""
           |$t ですよ!!
        """.stripMargin
    }
  println(LocalDate.of(2019, 1, 1).show)

  // catsのeqクラスを利用
  val eqInt = Eq[Int]
  println(eqInt.eqv(1, 1))
  println(eqInt.eqv(2, 1))
  // 演算子で表現
  println(1 === 1)
  println(2 === 1)

  // comparing Options型クラス
  val eqOptinInt = Eq[Option[Int]]
  println(eqOptinInt.eqv(Some(1), Some(1)))
  println(eqOptinInt.eqv(Some(1), Some(2)))
  println(eqOptinInt.eqv(Some(1), None))
  // 演算子で表現
  println((Some(1): Option[Int]) === (None))
  // 更に省略
  println(1.some === none[Int])

  implicit val dateEq: Eq[Date] =
    Eq.instance[Date] { (date1, date2) =>
      date1.getTime === date2.getTime
    }
  val x = new Date()
  val y = new Date()
  println(x === y)

}

final case class Person(name: String, email: String)

final case class Cat(name: String, age: Int, color: String)


object MyTypeJson {

  // type class
  sealed trait Json

  trait JsonWriter[A] {
    def write(value: A): Json
  }

  final case class JsObject(get: Map[String, Json]) extends Json

  final case class JsString(get: String) extends Json

  final case class JsNumber(get: Double) extends Json

  // 拡張クラスで追加
  object JsonSyntax {

    implicit class JsonWriterOps[A: JsonWriter](value: A) {
      //    def toJson(implicit w: JsonWriter[A]): Json =
      //      w.write(value)
      //  }
      def toJson: Json = implicitly[JsonWriter[A]].write(value)
    }

  }

  // type class InterFace
  object Json {
    //  def toJson[A](value: A)(implicit w: JsonWriter[A]): Json = w.write(value)
    def toJson[A: JsonWriter](value: A): Json =
      implicitly[JsonWriter[A]].write(value)
  }

  case object JsNull extends Json

  // type class Instances
  object JsonWriterInstances {
    implicit val stringWriter: JsonWriter[String] =
      new JsonWriter[String] {
        override def write(value: String): Json = JsString(value)
      }
    implicit val personWriter: JsonWriter[Person] =
      new JsonWriter[Person] {
        override def write(value: Person): Json =
          JsObject(Map(
            "name" -> JsString(value.name),
            "email" -> JsString(value.email)
          ))
      }

    implicit def optionWriter[A](implicit writer: JsonWriter[A]): JsonWriter[Option[A]] = new JsonWriter[Option[A]] {
      override def write(value: Option[A]): Json = value match {
        case Some(v) => writer.write(v)
        case None => JsNull
      }
    }

  }

}

