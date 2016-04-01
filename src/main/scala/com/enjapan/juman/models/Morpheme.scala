package com.enjapan.juman.models

/**
  * Created by Ugo Bataillard on 2/2/16.
  */

/**
  * The smallest unit in the sentence. Similar to a kuromoji token, usually a word.
  * @param midasi
  * @param yomi
  * @param genkei
  * @param hinsi
  * @param hinsiId
  * @param bunrui
  * @param bunruiId
  * @param katuyou1
  * @param katuyou1Id
  * @param katuyou2
  * @param katuyou2Id
  * @param imis
  * @param fstring
  */
case class Morpheme(
  midasi: String,
  yomi: String,
  genkei: String,
  hinsi: String,
  hinsiId: Int,
  bunrui: String,
  bunruiId: Int,
  katuyou1: String,
  katuyou1Id: Int,
  katuyou2: String,
  katuyou2Id: Int,
  imis: String,
  fstring: String
)
