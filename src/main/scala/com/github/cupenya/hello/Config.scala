package com.github.cupenya.hello

import com.typesafe.config.ConfigFactory

object Config {
  private val rootConfig = ConfigFactory.load()

  object http {
    private val httpConfig = rootConfig.getConfig("http")

    val interface = httpConfig.getString("interface")
    val port = httpConfig.getInt("port")
  }
}
