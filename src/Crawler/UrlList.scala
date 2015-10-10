package Crawler

/**
 * @author MD103
 */
abstract class UrlList {
  val urlList : List[Url]
  
  def isExist(url : Url)
  
}