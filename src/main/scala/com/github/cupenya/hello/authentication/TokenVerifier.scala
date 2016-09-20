package com.github.cupenya.hello.authentication

import com.github.cupenya.hello.{Config, Logging}
import pdi.jwt.{Jwt, JwtAlgorithm}

import scala.concurrent.{ExecutionContext, Future}
import spray.json._

trait TokenVerifier[T] {
  def verifyToken(token: String)(implicit ec: ExecutionContext): Future[T]
}

class JwtTokenVerifier extends TokenVerifier[AuthInfo] with DefaultJsonProtocol with Logging {

  implicit val authInfoFormat = jsonFormat5(AuthInfo)

  override def verifyToken(token: String)(implicit ec: ExecutionContext): Future[AuthInfo] = Future {
    Jwt.decode(token, Config.authentication.secret, Seq(JwtAlgorithm.HS256)).map { decodedJson =>
      log.debug(s"Decoded json token $decodedJson")
      decodedJson.parseJson.convertTo[AuthInfo]
    }.get
  }
}
