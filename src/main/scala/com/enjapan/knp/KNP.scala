package com.enjapan
package knp

import juman.{Juman, JumanClient}
import com.enjapan.backports._
import cats.data.Xor
import com.enjapan.juman.Juman._
import com.enjapan.knp.models.BList

import scala.language.postfixOps
import sys.process._

/**
  * Created by Ugo Bataillard on 2/2/16.
  */

/**
  * KNP helpers
  */
object KNP {

  val KNP_PATH = helpers.envProps.getOrElse("KNP_PATH", "knp")

  val KNP_FLAGS = List("-tab")
  val KNP_SERVER_FLAGS = "-F" :: KNP_FLAGS
  val DEFAULT_KNP_HOST = "127.0.0.1"
  val DEFAULT_KNP_PORT = 31000

  def main(args: Seq[String]): Unit = {
    val knp = new KNPCli
    knp(args.head) match {
      case Xor.Left(e) => throw e
      case Xor.Right(bList) =>
        bList.roots.foreach {_ traverse println}
    }
  }

  /**
    * Start a KNP server
    * @param command custom command to launch the KNP server
    * @return KNP server process
    */
  def startServer(command: Seq[String] = KNP_PATH +: KNP_SERVER_FLAGS): Process = SocketServer.startServer(command)

  /**
    * Helper for creating a KNP background server for the time of a block.
    * @param command custom command used to create the server
    * @param f block to evaluate which needs the server running
    * @tparam T block's return value type
    * @return block's return value
    */
  def withServer[T](command: Seq[String] = KNP_PATH +: KNP_SERVER_FLAGS)(f: () => T): T = SocketServer
    .withServer(command)(f)


  /**
    * Helper for creating a KNP client backed by both Juman and KNP servers the time of the evaluation of the block.
    * @param f block needing a KNPClient
    * @tparam T block's return value type
    * @return block's return value
    */
  def withServerBackedClient[T](f: KNPClient => T): T = {
    Juman.withServer() { () =>
      withServer() { () =>
        withClient()(f)
      }
    }
  }

  /**
    * Creates a KNP client backed by a Juman client for the time of a block evaluation.
    * To use this you need to have instances of both juman and knp servers running in the background.
    * @param knpHost knp server address
    * @param knpPort knp server port
    * @param jumanHost juman server address
    * @param jumanPort juman server port
    * @param f block needing a KNPClient
    * @tparam T block's return value type
    * @return block's return value
    */
  def withClient[T](knpHost: String = DEFAULT_KNP_HOST, knpPort: Int = DEFAULT_KNP_PORT, jumanHost: String = DEFAULT_JUMAN_HOST, jumanPort: Int = DEFAULT_JUMAN_PORT)(f: KNPClient => T): T = {
    Juman.withClient(jumanHost, jumanPort) { jumanClient =>
      withClient(jumanClient)(knpHost, knpPort) { c => f(c) }
    }
  }

  def withClient[T](jumanClient: JumanClient)(host: String, port: Int)(f: KNPClient => T): T = {
    val c = new KNPClient(jumanClient, host, port)
    val res = f(c)
    c.closeAll()
    res
  }
}

/**
  *
  */
trait KNP {
  val parser = new KNPParser()

  /**
    * Alias to parse
    * @param text
    * @return
    */
  def apply(text: String) = parse(text)

  /**
    * Parses a text with KNP
    * @param text text to parse
    * @return Either a representation of KNP output or a ParseException
    */
  def parse(text: String): Xor[ParseException, BList]
}

/**
  * This will launch an instance of juman and knp for each parsing.
  * @param knpCommand command used to launch knp
  * @param jumanCommand command used to launch juman
  */
class KNPCli(knpCommand: Seq[String] = KNP.KNP_PATH +: KNP.KNP_FLAGS, jumanCommand: Seq[String] = Seq(juman.Juman
  .JUMAN_PATH)) extends KNP {

  override def parse(text: String): Xor[ParseException, BList] = {
    val lines = Seq("echo", text) #| jumanCommand #| knpCommand lineStream

    parser.parse(lines)
  }
}
