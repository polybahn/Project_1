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
        val crawlres = get(cur_url)
        crawlPageLinks(cur_url, crawlres._1, new String(crawlres._2))
      }
    }
    //    val testData = Array[String]("http://idvm-infk-hofmann03.inf.ethz.ch/eth/www.ethz.ch/en/the-eth-zurich/education/quality-management.html","http://idvm-infk-hofmann03.inf.ethz.ch/eth/www.ethz.ch/en/the-eth-zurich/portrait/honorary-councillors-and-honorary-doctors.html")
    //    
    //    testData.foreach(t => crawlPageLinks(t, new String(get(t)._2)))

    println("Distinct URLs found: " + Statistic.NUM_OF_URLS)
    println("Exact duplicates found: " + Statistic.NUM_OF_IDENTICALS)
    println("Unique English pages found: " + Statistic.NUM_OF_EN_PAGE)
    println("Near duplicats found: " + Statistic.NUM_OF_DUPLICATES)
    println("Term frequency of \"student\":" + Statistic.NUM_OF_STUDENT)

  }

  private def crawlPageLinks(pageUrl: String, resultCode: Int, pageContent: String) {

    if (resultCode == 200) {
      Statistic.NUM_OF_URLS += 1
      if (!pageContent.equals("") && pageContent.length() > 0 && !pageUrl.contains("login")) {
        //      println(pageUrl)
        val doc: Document = new Document(pageUrl, pageContent)

        var isIdentical: Boolean = false;
        if (DetecterNew.isExist(doc.fingerprint)._1) {
          if (doc.fingerprint != 1328805421) {
            //                            println("2____" + doc.fingerprint + ": " + pageUrl)
            val a = new Document(DetecterNew.isExist(doc.fingerprint)._2, new String(get(DetecterNew.isExist(doc.fingerprint)._2)._2))
            val b = new Document(pageUrl, new String(get(pageUrl)._2))
            if (a.shingles == b.shingles) {
              println("1_____" + a.fingerprint + a.url)
              println("2_____" + b.fingerprint + b.url)
              Statistic.NUM_OF_IDENTICALS += 1
              isIdentical = true
            } else {
              isIdentical = true
            }
          }
        }
        if (!isIdentical) {
          if (DetecterNew.isNearDuplicate(doc.fingerprint)) {
            Statistic.NUM_OF_DUPLICATES += 1
          } else {
            if (doc.isEn) {
              Statistic.NUM_OF_EN_PAGE += 1
              Statistic.NUM_OF_STUDENT += doc.num_of_student
            }
          }
          DetecterNew.add(doc.fingerprint)
          DetecterNew.add_to_print(doc.fingerprint, pageUrl)
          val links = Parser.extractOutLinks(pageUrl, pageContent)
          links.map { link => if (!CrawledUrl.isExist(link)) queue.put(link) }
        }
      }
    }
  }

  private def get(url: String) = {
    val uri = new URL(url);
    val conn = uri.openConnection().asInstanceOf[HttpURLConnection];
    conn.setConnectTimeout(10000)
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

        (conn.getResponseCode(), data)
      } else {
        //        println(url)
        conn.disconnect
        (conn.getResponseCode(), Array[Byte]())
      }
    } catch {
      case e: Exception => {
        conn.disconnect
        (-1, Array[Byte]())
      }
    }
  }

}