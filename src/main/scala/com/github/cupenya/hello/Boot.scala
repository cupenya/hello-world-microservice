package com.github.cupenya.hello

import akka.actor.{ ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

object Boot extends App with Logging with HelloHttpService {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()

  log.info(s"Starting Hello service")

  Http().bindAndHandle(helloRoute, Config.http.interface, Config.http.port).transform(
    binding => log.info(s"REST interface bound to ${binding.localAddress} "), { t => log.error(s"Couldn't start Hello service", t); sys.exit(1) }
  )

}
