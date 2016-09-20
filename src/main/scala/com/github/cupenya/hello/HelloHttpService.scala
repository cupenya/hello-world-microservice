package com.github.cupenya.hello

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import spray.json._

import scala.concurrent.ExecutionContext

case class Hello(message: String)

trait Protocols extends DefaultJsonProtocol {
  implicit val helloFormat = jsonFormat1(Hello)
}

trait HelloHttpService extends Directives with SprayJsonSupport with Protocols with Logging {

  implicit val ec: ExecutionContext

  val helloRoute =
    pathPrefix("hello") {
      get {
        complete(Hello("hello world!"))
      }
    }
}
