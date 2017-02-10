package com.github.cupenya.hello

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives

trait LivenessProbeHttpService extends Directives with SprayJsonSupport with Protocols with Logging {
  val livenessRoute =
    pathEndOrSingleSlash {
      get {
        complete(OK -> None)
      }
    }
}
