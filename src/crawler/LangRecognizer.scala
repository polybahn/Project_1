package crawler

import java.util.Dictionary
import scala.io.Source
import java.io.File
import scala.collection.mutable
import scala.math.log

/**
 * @author MD103
 */
object LangRecognizer {
  val path_EN : String = "europarl-v6.de-en.en"
  val path_DE : String = "europarl-v6.de-en.en"
  val dict_EN : Set[String] = gatherDictionary(new File(path_EN))
  val dict_DE : Set[String] = gatherDictionary(new File(path_DE))
  
  val modelEN : Map[String, Int] = estimateProbability(new File(path_EN), dict_EN)
  val modelDE : Map[String, Int] = estimateProbability(new File(path_DE), dict_DE)  
  def main(val args : Array[String]){
    
  }
  
  def isEn(text: String): Boolean = {
    var scoreEN: Double = scoreSentence(text, modelEN)
    var scoreDE: Double = scoreSentence(text, modelEN)
    if(scoreEN > scoreDE) true else false
  }
  
  
  /** Simply includes all tokens in all documents in the dictionary
   */
  def gatherDictionary(file : File) : Set[String] = {
    val dict = mutable.Set[String]()
    
    for{line <- Source.fromFile(file).getLines()
      token <- tokenize(line)}
      dict += token

    dict.toSet
  }
  
  
  /** Simple add-one smoothed counts for all words in the dictionary
   */
  private def estimateProbability(file : File, dict : Set[String]) : Map[String, Int] = {
    // Initial zero counts
    val globalCounts = mutable.Map[String, Int]()
    globalCounts ++= dict.map(tok => (tok, 0))
    
    val lineIter = Source.fromFile(file).getLines()
    
    // Gather counts for all words
    for(line <- lineIter){
      // Let Scala do simple tokenization and check if we know the words
      val tokens = tokenize(line).filter(tok => dict.contains(tok))
      // Count how often every word appears
      val lineCounts = tokens.groupBy(identity).mapValues(_.size)
      // ++= will simply overwrite existing values
      globalCounts ++= lineCounts.map{case (t,c) => (t, c + globalCounts.getOrElse(t, 0))}
    }    
    globalCounts.toMap
  }
  /** Score a sentence under a certain model. Exclude tokens not in the dictionary.
   */
  def scoreSentence(sentence : String, model : Map[String, Int]) : Double = {
    val total: Double = model.values.sum.toDouble
    tokenize(sentence).map(tok => log((model.getOrElse(tok, 0)+1)/(total+1))).sum
  }
  /** Simple tokenization
   */ 
  def tokenize(text : String)  = text.split("\\W+")
}