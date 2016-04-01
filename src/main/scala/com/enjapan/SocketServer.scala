package com.enjapan

import scala.sys.process._
import scala.util.Try

/**
  * Created by Ugo Bataillard on 3/14/16.
  */

/**
  * Helpers for creating servers
  */
object SocketServer {

  /**
    * Starts a background process and adds a hook to kill it at JVM shutdown.
    * @param command command to run in the background
    * @return the background process
    */
  def startServer(command: Seq[String]): Process = {
    val bgProc = command.run()
    sys addShutdownHook {
      bgProc.destroy
    }
    bgProc
  }

  /**
    * Helper for creating a background server for the time of a block.
    * @param command command used to create the server
    * @param f block to evaluate which needs the server running
    * @tparam T block's return value type
    * @return block's return value
    */
  def withServer[T](command: Seq[String])(f: () => T): T = {
    val proc = startServer(command)
    Thread.sleep(100)
    val res = Try {f()}
    proc.destroy()
    res.get
  }

}
