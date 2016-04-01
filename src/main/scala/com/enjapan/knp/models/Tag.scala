package com.enjapan.knp.models

import com.enjapan.juman.models.Morpheme

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
case class Pas(cfid: String, arguments: Map[String, PredicateArgumentAnalysis])

case class PredicateArgumentAnalysis(
  relationName: String,
  relationType: String,
  argWord: String,
  argId: Int,
  argSentId: String) {

  private var _arg: Tag = null

  protected[knp] def arg_=(tag: Tag): Unit = {
    _arg = tag
  }

}

case class Rel(
  `type`: String,
  target: String,
  sid: String,
  id: Option[Int],
  mode: Option[String]
)

/**
  * Bunsetsu sub division
  *
  * @param parentId
  * @param dpndtype
  * @param fstring
  * @param paTypes
  * @param morphemes
  * @param features
  * @param rels
  * @param pas
  */
case class Tag(
  parentId: Int,
  dpndtype: String,
  fstring: String,
  paTypes: List[PAType],
  morphemes: List[Morpheme],
  features: Map[String, String],
  rels: List[Rel],
  pas: Option[Pas]) extends KNPNode[Tag] {

  /**
    * Builds the written representation of the tag
    *
    * @return
    */
  def surface: String = morphemes.map(_.midasi).mkString

}
