package com.enjapan
package juman

/**
  * Created by Ugo Bataillard on 3/9/16.
  */
class JumanClient(host: String = Juman.DEFAULT_JUMAN_HOST, port: Int = Juman
  .DEFAULT_JUMAN_PORT) extends SocketClient(host, port) {
  val command = "RUN -e2"
}
