package crawler

import Crawler.Statistic

/**
 * @author MD103
 */
object CrawledUrl {
  private val crawledPool = new java.util.HashSet[String]

  
  
  
  /*
   * To judge if an url is already crawled 
   */
  def isExist(url: String): Boolean = {
    if (crawledPool.contains(url)) {
      Statistic.NUM_OF_SAME_URLS += 1
      true
    } else {
      false
    }
  }
  
  def add(url : String): Unit = {
    crawledPool.add(url)
  }
  

}