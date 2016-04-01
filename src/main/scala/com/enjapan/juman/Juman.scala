package com.enjapan
package juman

import scala.sys.process._

/**
  * Created by Ugo Bataillard on 3/14/16.
  */

/**
  * Juman helpers
  */
object Juman {

  val JUMAN_PATH = helpers.envProps.getOrElse("JUMAN_PATH", "juman")
  val JUMAN_FLAGS = Seq()
  val JUMAN_SERVER_FLAGS = Seq("-F", "-S")
  val DEFAULT_JUMAN_HOST = "127.0.0.1"
  val DEFAULT_JUMAN_PORT = 32000

  /**
    * Starts a Juman server
    * @param command custom command to launch juman server.
    * @return Juman server process
    */
  def startServer(command: Seq[String] = JUMAN_PATH +: JUMAN_SERVER_FLAGS): Process = SocketServer.startServer(command)

  /**
    * Helper for creating a Juman background server for the time of a block.
    * @param command
    * @param f
    * @tparam T
    * @return
    */
  def withServer[T](command: Seq[String] = JUMAN_PATH +: JUMAN_SERVER_FLAGS)(f: () => T): T = SocketServer
    .withServer(command)(f)


  /**
    * Creates a Juman client for the time of a block evaluation.
    * @param host address of the juman server
    * @param port port of the juman server
    * @param f block needing a juman client
    * @tparam T block's return value type
    * @return block's return value
    */
  def withClient[T](host: String = DEFAULT_JUMAN_HOST, port: Int = DEFAULT_JUMAN_PORT)(f: JumanClient => T) = {
    val c = new JumanClient(host, port)
    val res = f(c)
    c.closeAll()
    res

  }
}
