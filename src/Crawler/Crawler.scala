package crawler

import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.CountDownLatch

import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.mapAsScalaMap

/**
 * @author MD103
 */
class Crawler(startPage: String = Enum.startPage) {
  private val latch = new CountDownLatch(10)

  def crawl {
    crawlPageLinks(startPage)

  }
  
  private def crawlPageLinks(pageUrl : String, pageContent : String) {
    
  }
  

  def get(url: String) = {
    val uri = new URL(url);
    val conn = uri.openConnection().asInstanceOf[HttpURLConnection];
    conn.setConnectTimeout(100000)
    conn.setReadTimeout(1000000)
    val stream = conn.getInputStream()
    val buf = Array.fill[Byte](1024)(0)
    var len = stream.read(buf)
    val out = new ByteArrayOutputStream
    while (len > -1) {
      out.write(buf, 0, len)
      len = stream.read(buf)
    }

    val data = out.toByteArray()
    val status = conn.getResponseCode()

    val headers = conn.getHeaderFields().toMap.map {
      head => (head._1, head._2.mkString(","))
    }
    conn.disconnect
    CrawledUrl.add(url)
    (conn.getResponseCode(), data, headers)
  }

}