package crawler

import lib.Tokenizer

/**
 * @author MD103
 */
class Document(val str: String) {
  type Shingle = List[String]
  private def getShingles(s: String, q: Int): Set[Shingle] = {
    require(q >= 1)
    Tokenizer.tokenize(s).sliding(q).toSet
  }

  /**
   * getHashValueList : Binary String
   */
  private def getSimhash(shingles: Set[Shingle]): Set[String] = {
    shingles.map(_.hashCode).map(h => binary(h))
    def binary(value: Int): String = {
      String.format("%"+Enum.fingerprintLen+"s", Integer.toBinaryString(value).replace(' ', '0'))
    }
    shingles.map(_.hashCode).map(h => binary(h))
  }

  
  /**
   * getHashValueList : Int
   */
  private def getSimhashValue(shingles: Set[Shingle]): List[Int] = {
    shingles.map(_.hashCode()).toList
  }
  
  /**
   * compute the fingerprint of this doc
   */
  private def getFingerPrint(S : Set[String]):String ={
    
    var fingerprint : String = ""
    for(i <- 0 to Enum.fingerprintLen-1){
      var sum : Int = 0
      val it = S.iterator
      while(it.hasNext){
        if(it.next()(i) == '1') sum+=1 else sum-=1
      }
      if(sum > 0)  fingerprint += '1' else fingerprint += '0'
    }
    fingerprint
  }  
  
  
  private def getFingerPrintValue(L : List[Int]): Int = {
    var res : Int = 0
    for (i <- List.range(0, Enum.fingerprintLen)){
      var sum = 0
      L.foreach(e => sum += (2*(e >> i & 1)-1)) 
      if(sum > 0) res = res|(1 << i) //set i_th bit to 1
      else res = res & ~(1 << i) //set i_th bit to 0
    }
    res
  }
  
  private def countWord(word : String) : Int ={
   val s : String = content.toLowerCase()
   var count = 0;  
   var index = 0;  
   while (index >= 0) {  
    index = s.indexOf(word, index + 1);  
    if (index > 0) {  
     count += 1;  
    } 
   }  
   count
  }
  
  def isMostInEn():Boolean ={
    true
  }
  
  
  // document content
  val content: String = str
  //define the number of words in one shingle
  val words_num_in_a_shingle: Int = 3
  // shingles in this document
  val shingles: Set[Shingle] = getShingles(str, words_num_in_a_shingle)
  // simHash value of shingles in this document
  val hashValue: List[Int] = getSimhashValue(shingles)
  // calculate fingerprint from hash values of shingles
//  val fingerprint: FingerPrint = new FingerPrint(Enum.fingerprintLen, hashValue)
  val fingerprint: Int = getFingerPrintValue(hashValue)
  // num of word "student"
  val num_of_student : Int = countWord(Enum.targetWord)
  // is this document mostly written in English
  val isEn : Boolean = isMostInEn()
  

//  val language: String
//  def extractUrls

}