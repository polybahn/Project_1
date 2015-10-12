package lib

import java.net.URL
import crawler.CrawledUrl
import java.util.regex.Pattern
import crawler.Enum

/**
 * @author MD103
 */
object Parser {
  private val linkRegex = """ (src|href)="([^"]+)"|(src|href)='([^']+)' """.trim.r
  private val htmlTypeRegex = "\btext/html\b"

  private def fetchLinks(text: String) = {
    val list = for (
      m <- linkRegex.findAllIn(text).matchData if (m.group(1) != null ||
        m.group(3) != null)
    ) yield {
      if (m.group(1) != null) m.group(2) else m.group(4)
    }
    list.filter {
      link =>  link.endsWith("html") && !link.startsWith("#") && !link.startsWith("javascript:") && !link.equals("") && !link.startsWith("mailto")
    }.toSet
  }

  /*
   * To get links pointing out from one html doc
   */
  def extractOutLinks(parentUrl: String, html: String) = {
    val baseHost = getHostBase(parentUrl)
    val links: Set[String] = fetchLinks(html).map {
      link =>
        link match {
          case link if link.startsWith("/") => baseHost + link
          case link if link.startsWith("http:") || link.startsWith("https:") => link
          case _ =>
            val index = parentUrl.lastIndexOf("/")
            parentUrl.substring(0, index) + "/" + link
        }
    }.filter {
      // only save not-crawled links
      link => !CrawledUrl.isExist(link) && link.contains(Enum.feature) 
    }
    links
  }

  private def getHostBase(url: String) = {
    val uri = new URL(url)
    val portPart = if (uri.getPort() == -1 || uri.getPort() == 80) "" else ":" + uri.getPort()
    uri.getProtocol() + "://" + uri.getHost() + portPart
  }

  def isTextPage(headers: Map[String, String]) = {
    val contentType = if (headers contains "content-type") headers("content-type") else null
    contentType match {
      case null => false
      case contentType if contentType isEmpty => false
      case contentType if Pattern.compile(htmlTypeRegex).matcher(contentType).find => true
      case _ => false
    }
  }

}