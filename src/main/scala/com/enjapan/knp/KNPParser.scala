package com.enjapan.knp

/**
  * Created by Ugo Bataillard on 2/2/16.
  */

import cats.data.Xor
import cats.std.list._
import cats.syntax.traverse._
import com.enjapan.juman.JumanParser
import com.enjapan.knp.models._

import scala.annotation.tailrec
import scala.collection.mutable
import scala.util.matching.Regex

/**
  * Created by Ugo Bataillard on 2/1/16.
  */
object KNPParser {
  val TAG: String = classOf[KNPParser].getSimpleName
  val SID_REGEX = """# S-ID:([^\s]*).*$""".r
  val BUNSETSU_REGEX = """\* (-?\d+)([DPIA])(.*)$""".r
  val TAG_REGEX = """\+ (-?\d+)(\w)(.*)$""".r
  val REL_REGEX =
    """rel type=\"([^\s]+?)\"(?: mode=\"([^>]+?)\")? target=\"([^\s]+?)\"(?: sid=\"(.+?)\" id=\"(.+?)\")?/""".r
  val WRITER_READER_LIST = Set("著者", "読者")
  val WRITER_READER_CONV_LIST = Map("一人称" -> "著者", "二人称" -> "読者")
}

case class ParseException(msg: String) extends Exception(msg)

/**
  *
  * @param breakingPattern
  */
class KNPParser(val breakingPattern: Regex = "^EOS$".r) {

  def parse(lines: Iterable[String]): Xor[ParseException, BList] = {

    val relevantLines = lines.map(_.trim)
      .filter(_.nonEmpty)
      .takeWhile(!breakingPattern.pattern.matcher(_).find)
      .filterNot(_.startsWith("EOS"))

    for {
      _ <- relevantLines.find(_.startsWith(";;")).fold(Xor.right[ParseException, Null](null)) { errorLine =>
        Xor.left(ParseException(s"Error found line starting with ';;: $errorLine"))
      }

      // Check if there can be only one comment
      csid = relevantLines.take(1).find(_.startsWith("#")).map { line =>
        val KNPParser.SID_REGEX(sid) = line
        (line, sid)
      }
      (comment, sid) = csid.getOrElse(("", ""))

      bunsetsus <- parseKNPNodeLines[Bunsetsu]("*", relevantLines.drop(if (csid.isDefined) 1 else 0), parseBunsetsu)

    } yield BList(breakingPattern.toString, comment, sid, bunsetsus.toIndexedSeq)
  }

  def parseBunsetsu(lines: Iterable[String]): ParseException Xor Bunsetsu = {
    for {
      r <- parseLine(lines.head, KNPParser.BUNSETSU_REGEX)
      (parentId, dpndtype, fstring, features, paTypes, s, ss) = r
      tags <- parseKNPNodeLines[Tag]("+", lines.drop(1), parseTag)
    } yield Bunsetsu(parentId, dpndtype, fstring, paTypes, features, tags)
  }

  def parseTag(lines: Iterable[String]): Xor[ParseException, Tag] = {
    for {
      r <- parseLine(lines.head, KNPParser.TAG_REGEX)
      (parentId, dpndtype, fstring, features, paTypes, rels, pas) = r
      morphemes <- (lines.drop(1) map JumanParser.parseMorpheme).toList.sequenceU
    } yield Tag(parentId, dpndtype, fstring, paTypes, morphemes, features, rels, pas)
  }

  def parseKNPNodeLines[T](
    prefix: String,
    lines: Iterable[String], parseNode: Iterable[String] => Xor[ParseException, T]): Xor[ParseException, List[T]] = {
    @tailrec
    def recParse(lines: Iterable[String], result: Xor[ParseException, List[T]]): Xor[ParseException, List[T]] = (lines, result) match {
      case (_, r: Xor.Left[ParseException]) => r
      case (ls, r) if ls.isEmpty => r
      case (ls, Xor.Right(r)) if ls.head.startsWith(prefix) =>
        val (nodeLines, remainingLines) = lines.tail.span(!_.startsWith(prefix))
        val node = parseNode(Iterable(ls.head) ++ nodeLines)
        recParse(remainingLines, node.map(_ :: r))
      case (ls, _) => Xor.Left(ParseException(s"Invalid line while parsing KNP node: $ls"))
    }
    recParse(lines, Xor.right(List.empty)).map(_.reverse)
  }

  def parseFeatures(fstring: String): (Map[String, String], List[Rel]) = {

    val spec: String = fstring.replaceAll("\\s+$", "").drop(1).dropRight(1)

    spec.split("><").foldLeft((Map.empty[String, String], List.empty[Rel])) {
      case ((features, rels), feature) =>
        if (feature.startsWith("rel")) {
          (features, rels ++ parseRel(feature))
        }
        else {
          val (key, v) = feature.span(_ != ':')
          val value = v.drop(1)
          (features + (key -> value), rels)
        }
    }
  }

  def parsePAS(value: String): Option[Pas] = {
    val cs = value.split(":").toList
    cs match {
      case c0 :: c1 :: c2 =>
        val cfid: String = c0 + ":" + c1
        val arguments = mutable.Map[String, PredicateArgumentAnalysis]()
        for {
          k <- c2.mkString("").split(";") if !c2.isEmpty
          items = k.split("/") if items.size > 5 && items(1) != "U" && items(1) != "-"
        } {
          arguments.put(items(0),
            PredicateArgumentAnalysis(
              relationName = items(0),
              relationType = items(1),
              argWord = items(2),
              argId = items(3).toInt,
              argSentId = items(5)
            ))
        }
        Some(Pas(cfid, arguments.toMap))
      case _ => None
    }
  }

  def parseRel(fstring: String, considerWriterReader: Boolean = false): Option[Rel] = {

    KNPParser.REL_REGEX
      .findAllMatchIn(fstring)
      .collectFirst {
        case m if m.group(4) != null && m.group(2) != "？" =>
          Rel(`type` = m.group(1),
            target = m.group(3),
            sid = m.group(4),
            id = Option(m.group(5)).map(_.toInt),
            mode = Option(m.group(2))
          )

        case m if considerWriterReader && m.group(2) != "？" && {
          val target = m.group(3)
          target != "なし" && KNPParser.WRITER_READER_LIST.contains(target) || KNPParser.WRITER_READER_CONV_LIST
            .contains(target)
        } =>
          val target = m.group(3)
          val t = if (KNPParser.WRITER_READER_LIST.contains(target)) target
          else KNPParser.WRITER_READER_CONV_LIST(target)
          Rel(`type` = m.group(1),
            target = t,
            sid = "",
            id = None,
            mode = Option(m.group(2))
          )
      }
  }

  def parseLine(line: String, linePattern: Regex): Xor[ParseException, (Int, String, String, Map[String, String], List[PAType], List[Rel], Option[Pas])] = {
    val l = line.trim
    val headers = Xor.fromOption({
      if (l.length == 1) {
        None
      }
      else {
        linePattern.findFirstMatchIn(l).map { m =>
          val parentId: Int = m.group(1).toInt
          val dpndtype: String = m.group(2)
          val fstring: String = m.group(3).trim
          (parentId, dpndtype, fstring)
        }
      }
    }, ParseException(s"Illegal KNP node spec: " + l))

    headers.map { case (parentId, dpndtype, fstring) =>
      val (features, rels) = parseFeatures(fstring)
      val paTypes = List.empty ++ features.get("用言").map(Predicate.apply) ++ features.get("体言").map(_ => Argument)
      val pas = features.get("格解析結果").flatMap(parsePAS)
      (parentId, dpndtype, fstring, features, paTypes, rels, pas)
    }
  }
}

