package crawler

import scala.collection.mutable.HashSet

/**
 * @author MD103
 */
object DetecterNew {

  /**
   * test exactly ones
   */
  def isExist(fingerprint: Int): Boolean = {
    if (fg_DB.contains(fingerprint)) {
      return true
    } else {
      return false
    }
  }
  /**
   * Using XOR to judge near duplicates
   */
  def isNearDuplicate(fingerprint: Int): Boolean = {
    def BitCount2(num: Int): Int = {
      var c: Int = 0;
      var temp = num
      while (temp != 0) {
        temp = temp & (temp - 1); // 清除最低位的1
        c += 1
      }
      c
    }
    var l: List[Int] = fg_DB.toList
    for (i <- List.range(0, fg_DB.size)) {
      if (BitCount2(l(i) ^ fingerprint) <= Enum.max_different_bits_neardups) {
        return true
      }
    }
    return false
  }

  def add(fingerprint : Int): Unit = {
    fg_DB += fingerprint
  }
  
  private var fg_DB: HashSet[Int] = new HashSet[Int]()
}