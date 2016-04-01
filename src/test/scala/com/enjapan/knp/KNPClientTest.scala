package com.enjapan.knp

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.enjapan.juman.JumanClient
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by Ugo Bataillard on 3/10/16.
  */
class KNPClientTest extends FunSuite with Matchers {

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
      """.stripMargin

    val jumanIn = new ByteArrayInputStream(jumanInputData.getBytes("UTF-8"))

    val knpInputData =
      """200 Running KNP Server
        |200 OK
        |# S-ID:1 KNP:4.2-1255337 DATE:2016/03/10 SCORE:-5.22923
        |* 1D <SM-主体><SM-場所><SM-組織><BGH:学校/がっこう><文頭><ヲ><助詞><体言><係:ヲ格><区切:0-0><格要素><連用要素><正規化代表表記:学校/がっこう><主辞代表表記:学校/がっこう>
        |+ 1D <SM-主体><SM-場所><SM-組織><BGH:学校/がっこう><文頭><ヲ><助詞><体言><係:ヲ格><区切:0-0><格要素><連用要素><名詞項候補><先行詞候補><正規化代表表記:学校/がっこう><解析格:ヲ>
        |学校 がっこう 学校 名詞 6 普通名詞 1 * 0 * 0 "代表表記:学校/がっこう カテゴリ:場所-施設 ドメイン:教育・学習" <代表表記:学校/がっこう><カテゴリ:場所-施設><ドメイン:教育・学習><正規化代表表記:学校/がっこう><漢字><かな漢字><名詞相当語><文頭><自立><内容語><タグ単位始><文節始><文節主辞>
        |を を を 助詞 9 格助詞 1 * 0 * 0 NIL <かな漢字><ひらがな><付属>
        |* -1D <BGH:探す/さがす><文末><句点><用言:動><レベル:C><区切:5-5><ID:（文末）><係:文末><提題受:30><主節><格要素><連用要素><動態述語><敬語:丁寧表現><正規化代表表記:探す/さがす><主辞代表表記:探す/さがす>
        |+ -1D <BGH:探す/さがす><文末><句点><用言:動><レベル:C><区切:5-5><ID:（文末）><係:文末><提題受:30><主節><格要素><連用要素><動態述語><敬語:丁寧表現><正規化代表表記:探す/さがす><用言代表表記:探す/さがす><時制-未来><主題格:一人称優位><クエリ削除語><格関係0:ヲ:学校><格解析結果:探す/さがす:動17:ガ/U/-/-/-/-;ヲ/C/学校/0/0/1;ノ/U/-/-/-/->
        |探して さがして 探す 動詞 2 * 0 子音動詞サ行 5 タ系連用テ形 14 "代表表記:探す/さがす" <代表表記:探す/さがす><正規化代表表記:探す/さがす><かな漢字><活用語><自立><内容語><タグ単位始><文節始><文節主辞>
        |い い いる 接尾辞 14 動詞性接尾辞 7 母音動詞 1 基本連用形 8 "代表表記:いる/いる" <代表表記:いる/いる><正規化代表表記:いる/いる><かな漢字><ひらがな><活用語><付属>
        |ます ます ます 接尾辞 14 動詞性接尾辞 7 動詞性接尾辞ます型 31 基本形 2 "代表表記:ます/ます" <代表表記:ます/ます><正規化代表表記:ます/ます><かな漢字><ひらがな><活用語><表現文末><付属>
        |。 。 。 特殊 1 句点 1 * 0 * 0 NIL <英記号><記号><文末><付属>
        |EOS
      """.stripMargin

    val knpIn = new ByteArrayInputStream(knpInputData.getBytes("UTF-8"))

    val jumanOut = new ByteArrayOutputStream()
    val knpOut = new ByteArrayOutputStream()


    val jumanClient = new JumanClient(jumanIn, jumanOut)
    val knpClient = new KNPClient(jumanClient, knpIn, knpOut)

    val stringToParse = "学校を探しています。"

    jumanClient.init()
    knpClient.init()
    val res = knpClient.parse(stringToParse)

    res should be ('right)
    val bTree = res.getOrElse(throw new Exception("Should not happen"))
    bTree.comment shouldBe knpInputData.split("\n").drop(2).head
    bTree.bunsetsuList should have size(2)

    jumanOut.toString("UTF-8") should be
      s"""
        |${jumanClient.command}
        |学校を探しています。
      """.stripMargin

    knpOut.toString("UTF-8") should be
    s"""
       |${knpClient.command}
       |${jumanInputData.lines.drop(1).mkString("\n")}
      """.stripMargin
  }
  */
}
