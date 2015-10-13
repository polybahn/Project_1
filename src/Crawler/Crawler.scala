package crawler

import java.net.HttpURLConnection
import java.net.HttpURLConnection
import java.net.URL
import java.net.URL
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import scala.annotation.migration
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.asScalaBuffer
import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.JavaConversions.mapAsScalaMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.ExecutionContext.Implicits.global
import lib.Parser
import java.io.ByteArrayOutputStream
/**
 * @author MD103
 */

object CrawlerTest extends App {
  new Crawler().crawl
}

class Crawler(startPage: String = Enum.startPage) {
  private val latch = new CountDownLatch(1)

  private var queue = new LinkedBlockingQueue[String]()

  def crawl {
    queue.put(startPage)
    while (!queue.isEmpty()) {
      val cur_url = queue.remove()
      if (cur_url != null && !CrawledUrl.isExist(cur_url)) {
        crawlPageLinks(cur_url, new String(get(cur_url)._2))
      }
    }

    println("Distinct URLs found: " + Statistic.NUM_OF_URLS)
    println("Exact duplicates found: " + Statistic.NUM_OF_IDENTICALS)
    println("Unique English pages found: " + Statistic.NUM_OF_EN_PAGE)
    println("Near duplicats found: " + Statistic.NUM_OF_DUPLICATES)
    println("Term frequency of \"student\":" + Statistic.NUM_OF_STUDENT)

  }

  private def crawlPageLinks(pageUrl: String, pageContent: String) {
    require(pageContent != null)
    Statistic.NUM_OF_URLS += 1
    println("Num_Of_Urls: " + Statistic.NUM_OF_URLS + " url: " + pageUrl)
    val doc: Document = new Document(pageContent)
    Statistic.NUM_OF_STUDENT += doc.num_of_student
    if (doc.isEn) Statistic.NUM_OF_EN_PAGE += 1

    if (DetecterNew.isExist(doc.fingerprint)) {
      Statistic.NUM_OF_IDENTICALS += 1
      //       println("identical found :"+  Statistic.NUM_OF_IDENTICALS + "in " + Statistic.NUM_OF_URLS +" pages")
    } else {
      if (DetecterNew.isNearDuplicate(doc.fingerprint)) {
        Statistic.NUM_OF_DUPLICATES += 1
        //        println("duplicate found :" + Statistic.NUM_OF_DUPLICATES + "in " + Statistic.NUM_OF_URLS + " pages")
      }
      DetecterNew.add(doc.fingerprint)
      val links = Parser.extractOutLinks(pageUrl, pageContent)
      links.map { link => if (!CrawledUrl.isExist(link)) queue.put(link) }
    }

    //    println("total:"+Statistic.NUM_OF_URLS + " iden: " + Statistic.NUM_OF_IDENTICALS +" near: "+Statistic.NUM_OF_DUPLICATES+" stu: "+Statistic.NUM_OF_STUDENT)
  }

  private def get(url: String) = {
    val uri = new URL(url);
    val conn = uri.openConnection().asInstanceOf[HttpURLConnection];
    conn.setConnectTimeout(100000)
    conn.setReadTimeout(1000000)

    try {
      if (conn.getResponseCode == 200) {
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
        //          println("wrapped Num _url " + Statistic.toString)
        conn.disconnect
        CrawledUrl.add(url)

        (conn.getResponseCode(), data, headers)
      } else {
        conn.disconnect
        (conn.getResponseCode(), Array[Byte](), null)
      }
    } catch {
      case e : Exception => {conn.disconnect
        (-1, Array[Byte](), null)
      }
    }
  }

}