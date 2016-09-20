package com.github.cupenya.hello

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{AuthorizationFailedRejection, Directives, RejectionHandler}
import akka.http.scaladsl.model.StatusCodes._
import com.github.cupenya.hello.authentication.AuthorizationDirectives
import spray.json._

import scala.concurrent.ExecutionContext

case class Hello(message: String)

case class AuthError(error: String)

trait Protocols extends DefaultJsonProtocol {
  implicit val helloFormat = jsonFormat1(Hello)
}

trait HelloHttpService extends Directives with AuthorizationDirectives with SprayJsonSupport with Protocols with Logging {
  implicit val ec: ExecutionContext

  implicit val authErrorFormat = jsonFormat1(AuthError)

  private val rh = RejectionHandler.newBuilder().handle {
    case AuthorizationFailedRejection =>
      complete(Forbidden -> AuthError("The supplied authentication is not authorized to access this resource"))
  }.result()

  val helloRoute = handleRejections(rh) {
    authorized { authInfo =>
      pathPrefix("hello") {
        get {
          complete(Hello(s"hello ${authInfo.userId}"))
        }
      }
    }
  }
}


