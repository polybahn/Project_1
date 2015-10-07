package Crawler
import java.io._
/**
 * @author MD103
 */
object testCrawler {
  def getIndex() = {
    val indexSource = scala.io.Source.fromURL("http://idvm-infk-hofmann03.inf.ethz.ch/eth/www.ethz.ch/en.html").mkString
    println(indexSource)
//    val indexRegex = """<a target="_blank" href="(.+\.html)" title=".+" >(.+)</a>""".r
//    (List[(String, String)] () /: indexRegex.findAllMatchIn(indexSource).toList) {
//      (result, item) =>
        
      
//    }
    
  }
  
  
  
  
  
  
  def main(args : Array[String]){
    getIndex;
  }
}

