package com.enjapan.knp

import cats.data.Xor
import com.enjapan.knp.models.{Predicate, Argument}
import org.scalatest.{FunSuite, Matchers}

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
class KNPParserTest extends FunSuite with Matchers {

  test("testParse") {
    val knpOutput =
      """# S-ID:123 KNP:4.2-ffabecc DATE:2015/04/10 SCORE:-18.02647
        |* 1D <BGH:解析/かいせき><文頭><サ変><助詞><連体修飾><体言>
        |+ 1D <BGH:構文/こうぶん><文節内><係:文節内><文頭><体言>
        |構文 こうぶん 構文 名詞 6 普通名詞 1 * 0 * 0 "代表表記:構文/こうぶん カテゴリ:抽象物" <代表表記:構文/こうぶん>
        |+ 2D <BGH:解析/かいせき><助詞><連体修飾><体言>
        |解析 かいせき 解析 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:解析/かいせき カテゴリ:抽象物 ドメイン:教育・学習;科学・技術" <代表表記:解析/かいせき>
        |の の の 助詞 9 接続助詞 3 * 0 * 0 NIL <かな漢字><ひらがな><付属>
        |* 2D <BGH:実例/じつれい><ヲ><助詞><体言><係:ヲ格>
        |+ 3D <BGH:実例/じつれい><ヲ><助詞><体言><係:ヲ格>
        |実例 じつれい 実例 名詞 6 普通名詞 1 * 0 * 0 "代表表記:実例/じつれい カテゴリ:抽象物" <代表表記:実例/じつれい>
        |を を を 助詞 9 格助詞 1 * 0 * 0 NIL <かな漢字><ひらがな><付属>
        |* -1D <BGH:示す/しめす><文末><句点><用言:動>
        |+ -1D <BGH:示す/しめす><文末><句点><用言:動>
        |示す しめす 示す 動詞 2 * 0 子音動詞サ行 5 基本形 2 "代表表記:示す/しめす" <代表表記:示す/しめす><正規化代表表記:示す/しめす>
        |。 。 。 特殊 1 句点 1 * 0 * 0 NIL <英記号><記号><文末><付属>
        |EOS""".stripMargin

    val parser = new KNPParser()

    val res = parser.parse(knpOutput.split("\n"))
    res.isRight shouldBe true
    val blist = res.getOrElse(throw new Exception("Should not happen"))

    val bunsetsuList = blist.bunsetsuList
    val tagList = blist.tagList
    val morphemeList = blist.morphemeList

    bunsetsuList should have size 3
    tagList should have size 4
    morphemeList should have size 7

    morphemeList.map(_.midasi).mkString("") shouldBe "構文解析の実例を示す。"
    blist.sid shouldBe "123"

    bunsetsuList(0).paTypes should contain theSameElementsAs List(Argument)
    bunsetsuList(1).paTypes should contain theSameElementsAs List(Argument)
    bunsetsuList(2).paTypes should contain theSameElementsAs List(Predicate("動"))

    // Check parent / children relations
    bunsetsuList(1).parentId shouldBe 2
    bunsetsuList(1).parent shouldBe Some(bunsetsuList(2))
    bunsetsuList(2).parentId shouldBe -1
    bunsetsuList(2).parent shouldBe None
    bunsetsuList(1).children should contain theSameElementsAs Seq(bunsetsuList(0))
    bunsetsuList(0).children should be('empty)

    tagList(1).parent.get shouldBe tagList(2)
    tagList(2).children should contain theSameElementsAs Seq(tagList(1))
  }

  test("testParseWeird") {

    val knpOutput =
      """# S-ID:1 KNP:4.2-1255337 DATE:2016/03/14 SCORE:-21.45878
        |* 1D <BGH:サイト/さいと><文頭><ガ><助詞><体言><係:ガ格><区切:0-0><格要素><連用要素><正規化代表表記:サイト/さいと><主辞代表表記:サイト/さいと>
        |+ 1D <BGH:サイト/さいと><文頭><ガ><助詞><体言><係:ガ格><区切:0-0><格要素><連用要素><名詞項候補><先行詞候補><正規化代表表記:サイト/さいと><解析格:ガ>
        |サイト さいと サイト 名詞 6 普通名詞 1 * 0 * 0 "代表表記:サイト/さいと カテゴリ:場所-その他 ドメイン:家庭・暮らし" <代表表記:サイト/さいと><カテゴリ:場所-その他><ドメイン:家庭・暮らし><正規化代表表記:サイト/さいと><記英数カ><カタカナ><名詞相当語><文頭><自立><内容語><タグ単位始><文節始><固有キー><文節主辞>
        |が が が 助詞 9 格助詞 1 * 0 * 0 NIL <かな漢字><ひらがな><付属>
        |* 2D <BGH:する/する><サ変><サ変動詞><時制-過去><連体修飾><用言:動><係:連格><レベル:B><区切:0-5><ID:（動詞連体）><連体節><動態述語><正規化代表表記:リニューアル/りにゅーある><主辞代表表記:リニューアル/りにゅーある>
        |+ 2D <BGH:する/する><サ変動詞><時制-過去><連体修飾><用言:動><係:連格><レベル:B><区切:0-5><ID:（動詞連体）><連体節><動態述語><サ変><正規化代表表記:リニューアル/りにゅーある><用言代表表記:リニューアル/りにゅーある><格関係0:ガ:サイト><格関係2:外の関係:?><格解析結果:リニューアル/りにゅーある:動3:ガ/C/サイト/0/0/1;ヲ/U/-/-/-/-;ニ/U/-/-/-/-;ノ/U/-/-/-/-;外の関係/N/?/2/0/1>
        |リニューアル りにゅーある リニューアル 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:リニューアル/りにゅーある カテゴリ:抽象物 ドメイン:ビジネス" <代表表記:リニューアル/りにゅーある><カテゴリ:抽象物><ドメイン:ビジネス><正規化代表表記:リニューアル/りにゅーある><記英数カ><カタカナ><名詞相当語><サ変><サ変動詞><自立><内容語><タグ単位始><文節始><固有キー><文節主辞>
        |した した する 動詞 2 * 0 サ変動詞 16 タ形 10 "代表表記:する/する 付属動詞候補（基本） 自他動詞:自:成る/なる" <代表表記:する/する><付属動詞候補（基本）><自他動詞:自:成る/なる><正規化代表表記:する/する><かな漢字><ひらがな><活用語><とタ系連用テ形複合辞><付属>
        |ような ような ようだ 助動詞 5 * 0 ナ形容詞 21 ダ列基本連体形 3 NIL <かな漢字><ひらがな><活用語><付属>
        |* -1D <文末><体言><用言:判><体言止><レベル:C><区切:5-5><ID:（文末）><裸名詞><提題受:30><主節><状態述語><正規化代表表記:?/?><主辞代表表記:?/?>
        |+ -1D <文末><体言><用言:判><体言止><レベル:C><区切:5-5><ID:（文末）><裸名詞><提題受:30><主節><状態述語><判定詞句><名詞項候補><先行詞候補><正規化代表表記:?/?><用言代表表記:?/?><時制-無時制><解析連格:外の関係><格解析結果:?/?:判0>
        |? ? ? 名詞 6 普通名詞 1 * 0 * 0 "疑似代表表記 代表表記:?/? 品詞変更:?-?-?-15-1-0-0" <疑似代表表記><代表表記:?/?><正規化代表表記:?/?><品詞変更:?-?-?-15-1-0-0-"疑似代表表記 代表表記:?/?"><品曖-その他><未知語><記英数カ><英記号><記号><名詞相当語><文末><表現文末><自立><内容語><タグ単位始><文節始><文節主辞>
        |EOS
      """.stripMargin


    val parser = new KNPParser()

    val res = parser.parse(knpOutput.split("\n"))
    res.isRight shouldBe true
    val blist = res.getOrElse(throw new Exception("Should not happen"))
  }

  test("testParseWeirder") {
    val knpOutput =
      """# S-ID:1 KNP:4.2-1255337 DATE:2016/03/15 SCORE:-17.70664
        |* 1P <BGH:時間/じかん><文頭><時間><強時間><ト><助詞><体言><係:ト格><並キ:名><区切:1-2><並列タイプ:AND><格要素><連用要素><正規化代表表記:時間/じかん><主辞代表表記:時間/じかん><並列類似度:1.258><並結句数:2><並結文節数:1>
        |+ 2P <BGH:時間/じかん><文頭><時間><強時間><ト><助詞><体言><係:ト格><並キ:名><区切:1-2><並列タイプ:AND><格要素><連用要素><名詞項候補><正規化代表表記:時間/じかん>
        |時間 じかん 時間 名詞 6 時相名詞 10 * 0 * 0 "代表表記:時間/じかん カテゴリ:時間" <代表表記:時間/じかん><カテゴリ:時間><正規化代表表記:時間/じかん><漢字><かな漢字><名詞相当語><文頭><自立><内容語><タグ単位始><文節始><文節主辞>
        |と と と 助詞 9 格助詞 1 * 0 * 0 NIL <かな漢字><ひらがな><付属>
        |* 2D <BGH:交通/こうつう+費/ひ><サ変><体言><係:未格><隣係絶対><用言一部><裸名詞><区切:0-0><格要素><連用要素><正規化代表表記:交通/こうつう+費/ひ><主辞代表表記:費/ひ><主辞’代表表記:交通/こうつう+費/ひ>
        |+ 2D <BGH:交通/こうつう><文節内><係:文節内><サ変><体言><名詞項候補><先行詞候補><非用言格解析:動><照応ヒント:係><態:未定><正規化代表表記:交通/こうつう>
        |交通 こうつう 交通 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:交通/こうつう カテゴリ:抽象物 ドメイン:交通" <代表表記:交通/こうつう><カテゴリ:抽象物><ドメイン:交通><正規化代表表記:交通/こうつう><漢字><かな漢字><名詞相当語><サ変><自立><内容語><タグ単位始><文節始>
        |+ 3D <BGH:交通/こうつう+費/ひ><体言><係:未格><隣係絶対><用言一部><裸名詞><区切:0-0><格要素><連用要素><一文字漢字><名詞項候補><先行詞候補><正規化代表表記:費/ひ><解析格:ヲ>
        |費 ひ 費 接尾辞 14 名詞性名詞接尾辞 2 * 0 * 0 "代表表記:費/ひ 内容語 カテゴリ:人工物-金銭 同義:名詞:費用/ひよう" <代表表記:費/ひ><内容語><カテゴリ:人工物-金銭><同義:名詞:費用/ひよう><正規化代表表記:費/ひ><漢字><かな漢字><名詞相当語><付属><タグ単位始><文節主辞>
        |* -1D <BGH:返す/かえす><文末><態:ほしい><〜ほしい><句点><用言:動><レベル:C><区切:5-5><ID:（文末）><係:文末><提題受:30><主節><格要素><連用要素><動態述語><モダリティ-依頼Ｂ><正規化代表表記:返す/かえす><主辞代表表記:返す/かえす>
        |+ -1D <BGH:返す/かえす><文末><態:ほしい><〜ほしい><句点><用言:動><レベル:C><区切:5-5><ID:（文末）><係:文末><提題受:30><主節><格要素><連用要素><動態述語><モダリティ-依頼Ｂ><正規化代表表記:返す/かえす><用言代表表記:返す/かえす+欲しい/ほしい><時制-未来><主題格:一人称優位><格関係2:ヲ:費><格解析結果:返す/かえす+欲しい/ほしい:動3:ガ/U/-/-/-/-;ヲ/N/時間/0/0/1;ヲ/N/費/2/0/1;ニ/U/-/-/-/-;デ/U/-/-/-/-;ノ/U/-/-/-/->
        |返して かえして 返す 動詞 2 * 0 子音動詞サ行 5 タ系連用テ形 14 "代表表記:返す/かえす 付属動詞候補（基本） 自他動詞:自:返る/かえる" <代表表記:返す/かえす><付属動詞候補（基本）><自他動詞:自:返る/かえる><正規化代表表記:返す/かえす><かな漢字><活用語><自立><内容語><タグ単位始><文節始><文節主辞>
        |ほしい ほしい ほしい 形容詞 3 * 0 イ形容詞イ段 19 基本形 2 "代表表記:欲しい/ほしい 動詞派生:欲する/ほっする" <代表表記:欲しい/ほしい><動詞派生:欲する/ほっする><正規化代表表記:欲しい/ほしい><かな漢字><ひらがな><活用語><表現文末><付属>
        |。 。 。 特殊 1 句点 1 * 0 * 0 NIL <英記号><記号><文末><付属>
        |EOS
      """.stripMargin

    val parser = new KNPParser()

    val res = parser.parse(knpOutput.split("\n"))
    res.isRight shouldBe true
    val blist = res.getOrElse(throw new Exception("Should not happen"))
    blist.bunsetsuList should have size 3
    blist.tagList should have size 4
    blist.morphemeList should have size 7
  }

  test("testParseBunsetsu") {
    val bunsetsuString =
      """* -1D <BGH:解析/かいせき><文頭><文末><サ変><体言><用言:判><体言止><レベル:C>
        |+ 1D <BGH:構文/こうぶん><文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:構文/こうぶん>
        |構文 こうぶん 構文 名詞 6 普通名詞 1 * 0 * 0 "代表表記:構文/こうぶん カテゴリ:抽象物" <代表表記:構文/こうぶん>
        |+ -1D <BGH:解析/かいせき><文末><体言><用言:判><体言止><レベル:C>
        |解析 かいせき 解析 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:解析/かいせき カテゴリ:抽象物 ドメイン:教育・学習;科学・技術" <代表表記:解析/かいせき>""".stripMargin

    val parser = new KNPParser()

    val res = parser.parseBunsetsu(bunsetsuString.split("\n"))

    res shouldBe a[Xor.Right[_]]
    val bnst = res.getOrElse(throw new Exception("Should not happen"))

    bnst.parentId shouldBe -1
    bnst.dpndtype shouldBe "D"
    bnst.paTypes should contain theSameElementsAs List(Predicate("判"), Argument)
    bnst.repName shouldBe None
    bnst.features should have size 8
    bnst.tags should have size 2
    bnst.tags.map(_.dpndtype) should contain only "D"
    bnst.tags.flatMap(_.morphemes) should have size 2
  }

  test("testParseBunsetsuInvalid") {
    val bunsetsuString =
      """* -1D <BGH:解析/かいせき><文頭><文末><サ変><体言><用言:判><体言止><レベル:C>
        |構文 こうぶん 構文 名詞 6 普通名詞 1 * 0 * 0 "代表表記:構文/こうぶん カテゴリ:抽象物" <代表表記:構文/こうぶん>
        |+ 1D <BGH:構文/こうぶん><文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:構文/こうぶん>
        |+ -1D <BGH:解析/かいせき><文末><体言><用言:判><体言止><レベル:C>
        |解析 かいせき 解析 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:解析/かいせき カテゴリ:抽象物 ドメイン:教育・学習;科学・技術" <代表表記:解析/かいせき>"""
        .stripMargin.split("\n")
    val parser = new KNPParser()
    val res = parser.parseBunsetsu(bunsetsuString)
    res shouldBe a[Xor.Left[_]]
  }

  test("testParseTag") {
    val tagStr =
      """+ 1D <BGH:構文/こうぶん><文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:構文/こうぶん>
        |構文 こうぶん 構文 名詞 6 普通名詞 1 * 0 * 0 "代表表記:構文/こうぶん カテゴリ:抽象物" <代表表記:構文/こうぶん>
        |解析 かいせき 解析 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:解析/かいせき カテゴリ:抽象物 ドメイン:教育・学習;科学・技術" <代表表記:解析/かいせき> """
        .stripMargin.split("\n")

    val parser = new KNPParser()
    val res = parser.parseTag(tagStr)
    res shouldBe a[Xor.Right[_]]
    val tag = res.getOrElse(throw new Exception("Should not happen"))

    tag.dpndtype shouldBe "D"
    tag.parentId shouldBe 1
    tag.morphemes should have size 2
    tag.surface shouldBe "構文解析"
    tag.paTypes should contain theSameElementsAs List(Argument)
    tag.repName shouldBe Some("構文/こうぶん")
  }

  test("testParseTagInvalid") {
    val tagStr =
      """+ 1- <BGH:構文/こうぶん><文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:構文/こうぶん>
        |構文 こうぶん 構文 名詞 6 普通名詞 1 * 0 * 0 "代表表記:構文/こうぶん カテゴリ:抽象物" <代表表記:構文/こうぶん>
        |解析 かいせき 解析 名詞 6 サ変名詞 2 * 0 * 0 "代表表記:解析/かいせき カテゴリ:抽象物 ドメイン:教育・学習;科学・技術" <代表表記:解析/かいせき> """
        .stripMargin.split("\n")

    val parser = new KNPParser()
    val res = parser.parseTag(tagStr)
    res shouldBe a[Xor.Left[_]]
  }

  test("parseFeatures") {
    val tagStr = "<BGH:構文/こうぶん><文節内><係:文節内><文頭><体言><名詞項候補><先行詞候補><正規化代表表記:構文/こうぶん>"
    val parser = new KNPParser()
    val (f, _) = parser.parseFeatures(tagStr)
    f("BGH") shouldBe "構文/こうぶん"
    f("係") shouldBe "文節内"
    f.get("先行詞候補") should be('defined)
    f.get("dummy") shouldBe None
    f("正規化代表表記") shouldBe "構文/こうぶん"
  }

  test("testParsePAS") {

    val pasStr = "分/ふん:判1:ガ/U/-/-/-/-;ヲ/U/-/-/-/-;ニ/U/-/-/-/-;デ/C/車/1/0/14;カラ/U/-/-/-/-;ヨリ/C/インター/0/0/14;マデ/U/-/-/-/-;ヘ/U/-/-/-/-;時間/U/-/-/-/-"
    val parser = new KNPParser()
    val (Some(pas)) = parser.parsePAS(pasStr)

    pas.cfid shouldBe "分/ふん:判1"
    val args = pas.arguments
    args should have size 2
    args("デ").relationName shouldBe "デ"
    args("デ").relationType shouldBe "C"
    args("デ").argId shouldBe 1
    args("デ").argWord shouldBe "車"
    args("デ").argSentId shouldBe "14"
    args.get("ガ") shouldBe None
  }

  test("testParseEmptyPAS"){
    val pasStr = "?/?:判0"
    val parser = new KNPParser()
    val (Some(pas)) = parser.parsePAS(pasStr)
    pas.cfid shouldBe "?/?:判0"
    pas.arguments should be ('empty)
  }

  test("testParseRels") {

    val tagStr =
      """<rel type="時間" target="一九九五年" sid="950101003-002" id="1"/>
        |<rel type="ヲ" target="衆院" sid="950101003-002" id="3"/>
        |<rel type="ガ" target="不特定:人1"/>
        |<rel type="時間" target="国会前" sid="950101003-asd" id="16"/>
      """.stripMargin.replace("\n", "")

    val parser = new KNPParser()
    val (_, rels) = parser.parseFeatures(tagStr)
    rels should have size 3
    val rel = rels.head

    rel.id shouldBe Some(1)
    rel.mode shouldBe None
    rel.`type` shouldBe "時間"
    rel.sid shouldBe "950101003-002"
    rel.target shouldBe "一九九五年"
  }

}
