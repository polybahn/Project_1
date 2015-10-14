package crawler

/**
 * @author MD103
 */
object Enum {
  val startPage: String = "http://idvm-infk-hofmann03.inf.ethz.ch/eth/www.ethz.ch/en.html"
  val feature: String = "idvm-infk-hofmann03"
  val targetWord: String = "student"

  val path_EN: String = "dictEN"
  val path_DE: String = "dictDE"

  val fingerprintLen: Int = 64

  val max_different_bits_neardups: Int = 1
  
    //define the number of words in one shingle
  val words_num_in_a_shingle: Int = 3
}