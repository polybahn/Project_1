package crawler

import java.net.URL
import java.util.regex.Pattern

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
      link => link.endsWith(".html")
    }.toSet
  }

  /*
   * To get links pointing out from one html doc
   */
  def extractOutLinks(parentUrl: String, html: String) = {
    val baseHost = getHostBase(parentUrl)

    def removeDotsFromUrl(url: String): String = {
      var url_withoutdots = url
      while (("/[^/]+/\\.\\./".r findFirstIn url_withoutdots) != None) {
        url_withoutdots = ("/[^/]+/\\.\\./".r findFirstIn url_withoutdots) match {
          case Some(dot_part) => url_withoutdots.replace(dot_part, "/")
          case None => url_withoutdots
        }
      }
      url_withoutdots
    }

    val links: Set[String] = fetchLinks(html).map {
      link =>
        //        println("0 "+link)
        link match {
          case link if link.startsWith("/") => baseHost + link
          case link if link.startsWith("http:") || link.startsWith("https:") => link
          case link if link.startsWith(".") =>
//          new URI(parentUrl).resolve(new URI(link)).getPath
//            println(new URL(new URL(parentUrl), link).toString)
            new URL(new URL(parentUrl), link).toString()
          //          case link if link.startsWith("../") =>removeDotsFromUrl(baseHost + link)
          case _ =>
            val index = parentUrl.lastIndexOf("/")
            parentUrl.substring(0, index) + "/" + link
          //            link
        }
    }.filter {
      // only save not-crawled links
      link => !CrawledUrl.isExist(link) && link.contains(Enum.feature)
    }
    //    links.map{
    //      link => println("1  "+ link)
    //    }
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