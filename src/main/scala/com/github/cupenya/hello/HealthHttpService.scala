package com.github.cupenya.hello

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.model.StatusCodes._
import spray.json.DefaultJsonProtocol

sealed trait HealthCheckModel

object HealthCheckModel extends DefaultJsonProtocol {
  implicit val healthCheckResultFormat = jsonFormat2(HealthCheckResult)
}

case class HealthCheckResult(name: String, status: String) extends HealthCheckModel

trait HealthHttpService extends Directives with SprayJsonSupport with Protocols with Logging {
  val healthRoute =
    pathPrefix("health") {
      get {
        complete(OK -> Map(
          "services" -> List(
            HealthCheckResult("service1", "ok")
          )
        ))
      }
    }
}
