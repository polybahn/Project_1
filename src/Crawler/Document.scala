package Crawler

/**
 * @author MD103
 */
abstract class Document {
  val fingerprint: String
  val language: String
  
  def computeHash()
  def recognizeLanguage(s : String) : String 
  def countWord(word: String)
  def extractUrls()
  
  /*
   * extract Shingles
   */
  def getFetures()
}