package crawler

import scala.collection.mutable.HashSet

/**
 * @author MD103
 */
object CrawledUrl {
  private val crawledPool = new HashSet[String]()

  /*
   * To judge if an url is already crawled 
   */
  def isExist(url: String): Boolean = {
    if (crawledPool.contains(url)) {
      true
    } else {
      false
    }
  }

  def add(url: String): Unit = {
    crawledPool.add(url)
  }

}