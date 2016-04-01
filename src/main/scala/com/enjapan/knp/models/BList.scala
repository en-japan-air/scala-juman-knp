package com.enjapan.knp.models

/**
  * Created by Ugo Bataillard on 2/2/16.
  */

/**
  * Represents a knp output parsed.
  *
  * @param pattern      pattern used to find the end of knp output
  * @param comment      first line of knp output
  * @param sid          identifies the knp output (only useful in server mode)
  * @param bunsetsuList list of bunsetsus
  */
case class BList(pattern: String, comment: String, sid: String, bunsetsuList: IndexedSeq[Bunsetsu]) {

  /**
    * Helper get all the tags in each bunsetsu.
    */
  lazy val tagList = {
    for {
      bnst <- bunsetsuList
      tag <- bnst.tags
    } yield tag
  }

  /**
    * Helper to get all the morphemes in each tags of each bunsetsu.
    */
  lazy val morphemeList = {
    for {
      bnst <- bunsetsuList
      tag <- bnst.tags
      morpheme <- tag.morphemes
    } yield morpheme
  }

  /**
    * Builds the tree of dependencies for bunsetsus and tags.
    */
  def setParentChild() = {
    for (bnst <- bunsetsuList) {

      if (bnst.parentId == -1) {
        bnst.parent = None
      }
      else {
        val parent = bunsetsuList(bnst.parentId)
        bnst.parent = Some(parent)
        parent.addChild(bnst)
      }

      for (tag <- bnst.tags) {
        if (tag.parentId == -1) {
          tag.parent = None
        }
        else {
          val parent = tagList(tag.parentId)
          tag.parent = Some(parent)
          parent.addChild(tag)
        }
      }
    }
    ()
  }

  setParentChild()

  override def toString: String = {
    return String.format("%s\n%s%s\n", comment, bunsetsuList.mkString(","), pattern)
  }

  /**
    * The root bunsetsu of the sentence
    */
  lazy val root = bunsetsuList.find(_.parentId == -1).get

  /**
    * Root bunsetsus of the sentence (TODO: check if more than one roots can happen)
    */
  lazy val roots = bunsetsuList.filter(_.parentId == -1)

}
