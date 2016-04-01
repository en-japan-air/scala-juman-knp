package com.enjapan

import java.io.PrintStream
import java.net.{InetSocketAddress, Socket}

import scala.collection.mutable
import scala.io.BufferedSource
import scala.util.Try

/**
  * Created by Ugo Bataillard on 3/9/16.
  */

/**
  * This class handles the management of socket clients. It acts as one client but actually creates a client per thread.
  * @param host
  * @param port
  */
abstract class SocketClient(host: String, port: Int) {

  val command: String
  val EOS: String = "EOS"

  private val clients = mutable.Map.empty[Long, ThreadUnsafeSocketClient]

  private def buildClient(id: Long) = synchronized {
    println(s"Creating client for $id")
    val c = new ThreadUnsafeSocketClient(command, EOS, host, port)
    clients.put(id, c)
    c
  }

  def client = {
    val id = Thread.currentThread().getId
    clients.getOrElse(id, buildClient(id))
  }

  def closeAll() = clients.foreach { case (_, c) => c.close() }

  def run(lines: List[String]): List[String] = {
    client.run(lines)
  }

}

/**
  * Contains the common logic for both juman and knp socket clients.
  * @param command
  * @param EOS
  * @param host
  * @param port
  */
private[enjapan] class ThreadUnsafeSocketClient(command: String, EOS: String, host: String, port: Int) {

  import com.enjapan.helpers.RichIterator

  val socket = new Socket(host, port)
  init()

  def in = new BufferedSource(socket.getInputStream).getLines()
  def out = new PrintStream(socket.getOutputStream)

  def init() = {
    val i = in
    val o = out
    println("Server info: " + i.next())

    o.println(command)
    o.flush()
    println("Server answer: " + i.next())
  }

  def run(lines: List[String]): List[String] = {
    if (socket.isClosed) {
      socket.connect(new InetSocketAddress(host, port))
      init()
    }
    val o = out
    val i = in
    lines foreach o.println
    o.flush()
    val res = i.takeUntil(!_.startsWith(EOS)).toList
    res
  }

  def close(): Unit = {
    socket.close()
  }
}


object SocketClient {

  /**
    * Helper opening a socket for the time of block.
    * @param host address to connect to
    * @param port port to connect to
    * @param f block taking a socket as argument to use during its evaluation
    * @tparam T block's return value type
    * @return block's return value
    */
  def withSocket[T](host: String, port: Int)(f: Socket => T): T = {
    Try {
      val socket = new Socket(host, port)

      val res = Try {
        f(socket)
      }

      if (!socket.isClosed) {
        try {
          socket.close()
        } catch {
          case t: Throwable =>
        }
      }
      res
    }.flatten.get
  }
}