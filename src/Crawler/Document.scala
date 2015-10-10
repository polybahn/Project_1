package crawler

import lib.Tokenizer

/**
 * @author MD103
 */
class Document (val str : String){
  type Shingle = List[String]
  private def getShingles(s : String, q : Int) : Set[Shingle] = {
    require(q >=  1)
    Tokenizer.tokenize(s).sliding(q).toSet
  }
  
  private def binary(value : Int) : String = {
    String.format("%16s", Integer.toBinaryString(value).replace(' ', '0'))
  }
  
  private def getSimhash(shingles : Set[Shingle]) : Set[String] = {
    shingles.map(_.hashCode).map(h => binary(h))
  }
  
  private def getOutUrl:List[Url]={
    
  }
  
  // document content
  val content : String = str
  //define the number of words in one shingle
  val words_num_in_a_shingle : Int = 3
  // shingles in this document
  val shingles : Set[Shingle] = getShingles(str, words_num_in_a_shingle)
  // simHash value of shingles in this document
  val hashValue : Set[String] = getSimhash(shingles)
  // urls pointing out from this document
  val outUrl : List[Url] = 
  
  
  
  
  
  
  val fingerprint: FingerPrint
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