package crawler

import crawler.Tokenizer

/**
 * @author MD103
 */
class Document(val url: String, val str: String) {
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
      String.format("%" + Enum.fingerprintLen + "s", Integer.toBinaryString(value).replace(' ', '0'))
    }
    shingles.map(_.hashCode).map(h => binary(h))
  }

  /**
   * getHashValueList : Int
   */
  private def getSimhashValue(shingles: Set[Shingle]): List[Int] = {
    shingles.map(_.hashCode()).toList
  }

  private def getSimhashValue64(shingles: Set[Shingle]): List[Long] = {
    shingles.map { s => hash(s.mkString) }.toList
  }

  // adapted from String.hashCode()
  private def hash(string: String): Long = {
    var h: Long = 1125899906842597L; // prime
    val len: Int = string.length();

    for (i <- List.range(0, len)) {
      h = 31 * h + string.charAt(i);
    }
    h;
  }

  /**
   * compute the fingerprint of this doc using simhash, return binary string(we didn't use this)
   */
  private def getFingerPrint(S: Set[String]): String = {

    var fingerprint: String = ""
    for (i <- 0 to Enum.fingerprintLen - 1) {
      var sum: Int = 0
      val it = S.iterator
      while (it.hasNext) {
        if (it.next()(i) == '1') sum += 1 else sum -= 1
      }
      if (sum > 0) fingerprint += '1' else fingerprint += '0'
    }
    fingerprint
  }
  /**
   * compute the fingerprint of this doc using simhash, return 64 bits representation
   */
  private def getFingerPrintValue(L: List[Long]): Long = {
    var res: Long = 0
    for (i <- List.range(0, Enum.fingerprintLen)) {
      var sum: Long = 0
      L.foreach(e => sum += (2 * (e >> i & 1) - 1))
      
      
      if (sum > 0) res = res | (1 << i) //set i_th bit to 1
      else res = res & ~(1 << i) //set i_th bit to 0
    }
    res
  }
  /**
   * compute the fingerprint of this doc using simhash, return 32 bits representation
   */
  private def getFingerPrintValue2(L: List[Int]): Int = {
    var res = 0
    for (i <- List.range(0, Enum.fingerprintLen)) {
      var count_1 = 0
      L.foreach(e => if ((e >> i & 1) == 1) count_1 += 1)
      if (count_1 % 2 == 0) res = res & ~(1 << i) //set i_th bit to 0
      else res = res | (1 << i) //set i_th bit to 1
    }
    res
  }

  private def countWord(word: String): Int = {
    val s: String = str.toLowerCase()
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

  private def extractMainContent(html: String): String = {
    var index1 = 0;
    var index2 = 0;
    index1 = html.indexOf("mainContent");
    index2 = html.indexOf("mainContent", index1 + 1)
    if (index1 > 0 && index2 > 0) {
      html.substring(index1 + 1, index2)
    } else {
      index1 = html.indexOf("contentMain");
      index2 = html.indexOf("contentMain", index1 + 1)
      if (index1 > 0 && index2 > 0) {
        html.substring(index1 + 1, index2).replaceAll("\\<[^\\>]*\\>", " ")
//        html.substring(index1 + 1, index2)
      }else{
        html.replaceAll("\\<[^\\>]*\\>", " ")
//        html
      }
    }
  }
  // document content
  val content: String = extractMainContent(str)
  // shingles in this document
  val shingles: Set[Shingle] = getShingles(content, Enum.words_num_in_a_shingle)
  // simHash value of shingles in this document
  val hashValue: List[Long] = getSimhashValue64(shingles)
  // calculate fingerprint from hash values of shingles
  //  val fingerprint: FingerPrint = new FingerPrint(Enum.fingerprintLen, hashValue)
  val fingerprint: Long = getFingerPrintValue(hashValue)
  // num of word "student"
  val num_of_student: Int = countWord(Enum.targetWord)
  // is this document mostly written in English
  val isEn: Boolean = LangRecognizer.isMostInEn(content)

  //  val language: String
  //  def extractUrls

}