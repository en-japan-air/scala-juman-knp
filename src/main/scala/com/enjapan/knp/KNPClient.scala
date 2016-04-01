package com.enjapan
package knp

import juman.JumanClient
import java.io.{InputStream, OutputStream}

import cats.data.Xor
import com.enjapan.knp.models.BList

class KNPClient(jumanClient: JumanClient, host: String = KNP.DEFAULT_KNP_HOST, port: Int = KNP
  .DEFAULT_KNP_PORT) extends SocketClient(host, port) with KNP {
  val command = "RUN -tab"

  override def parse(text: String): Xor[ParseException, BList] = {
    val cleaned = text.replace("\n", "").trim
    if (cleaned.isEmpty) {
      Xor.left(ParseException("Can not parse empty strings"))
    } else {
      val jumanRes = jumanClient.run(List(cleaned))
      println(s"Juman: $jumanRes")
      val knpRes = run(jumanRes)
      println(s"KNP: $knpRes")
      parser.parse(knpRes.toIterable)
    }
  }
}
