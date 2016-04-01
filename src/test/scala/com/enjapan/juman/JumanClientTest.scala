package com.enjapan.juman

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by Ugo Bataillard on 3/10/16.
  */
class JumanClientTest extends FunSuite with Matchers {

  //FIXME find a way to test the client with a socket
  /*
  test("testParse") {

    val jumanInputData =
      """200 Running JUMAN version: 7.01
        |200 OK
        |学校 がっこう 学校 名詞 6 普通名詞 1 * 0 * 0 "代表表記:学校/がっこう カテゴリ:場所-施設 ドメイン:教育・学習"
        |を を を 助詞 9 格助詞 1 * 0 * 0 NIL
        |探して さがして 探す 動詞 2 * 0 子音動詞サ行 5 タ系連用テ形 14 "代表表記:探す/さがす"
        |い い いる 接尾辞 14 動詞性接尾辞 7 母音動詞 1 基本連用形 8 "代表表記:いる/いる"
        |ます ます ます 接尾辞 14 動詞性接尾辞 7 動詞性接尾辞ます型 31 基本形 2 "代表表記:ます/ます"
        |。 。 。 特殊 1 句点 1 * 0 * 0 NIL
        |EOS
        |""".stripMargin

    val jumanIn = new ByteArrayInputStream(jumanInputData.getBytes("UTF-8"))
    val jumanOut = new ByteArrayOutputStream()

    val jumanClient = new JumanClient(jumanIn, jumanOut)

    val stringToParse = "学校を探しています。"

    jumanClient.init()
    val res = jumanClient.run(Iterator(stringToParse))

    res.toList should contain theSameElementsInOrderAs jumanInputData.lines.drop(2).toTraversable

    jumanOut.toString("UTF-8") should be
    s"""
       |${jumanClient.command}
       |${stringToParse}
      """.stripMargin

  }
  */
}

