package crawler

import java.io.File
import scala.collection.mutable
import scala.collection.mutable.Map
import scala.io.Source
import scala.math.log

/**
 * @author MD103
 */
object LangRecognizer {

  val modelEN: Map[String, Int] = readMap(Enum.path_EN)
  val modelDE: Map[String, Int] = readMap(Enum.path_DE)

  private def readMap(filepath: String): Map[String, Int] = {
    val m: Map[String, Int] = Map[String, Int]()
    var s: String = ""
    var i: Int = 0
    for (l <- scala.io.Source.fromFile(new File(filepath)).getLines()) {
      val temp: Array[String] = l.split(',')
      m += (temp(0) -> Integer.parseInt(temp(1)))
//      println(temp(0) +' '+Integer.parseInt(temp(1)))
    }
    m
  }

  def isMostInEn(text: String): Boolean = {
    /**
     * Score a sentence under a certain model. Exclude tokens not in the dictionary.
     */
    def scoreSentence(sentence: String, model: Map[String, Int]): Double = {
      def tokenize(text: String) = text.split("\\W+")
      val total: Double = model.values.sum.toDouble
      tokenize(sentence).map(tok => log( (model.getOrElse(tok, 0) + 1).toDouble / (total + 1) )).sum
    }
    var scoreEN: Double = scoreSentence(text, modelEN)
    var scoreDE: Double = scoreSentence(text, modelDE)
//    println(scoreEN + "     " + scoreDE)
    if (scoreEN > scoreDE) true else false
  }

}