package crawler

import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap

/**
 * @author MD103
 */
object DetecterNew {

  /**
   * test exactly ones
   */
  def isExist(fingerprint: Long): (Boolean, String) = {
    if (fg_DB.contains(fingerprint)) {
//      println("1____"+fingerprint + ": "+fg_MAP(fingerprint))
      return (true, fg_MAP(fingerprint))
    } else {
      return (false, null)
    }
  }
  /**
   * Using XOR to judge near duplicates
   */
  def isNearDuplicate(fingerprint: Long): Boolean = {
    def BitCount2(num: Long): Int = {
      var c: Int = 0;
      var temp = num
      while (temp != 0) {
        temp = temp & (temp - 1); // 清除最低位的1
        c += 1
      }
      c
    }
    var l: List[Long] = fg_DB.toList
    for (i <- List.range(0, fg_DB.size)) {
      if (BitCount2(l(i) ^ fingerprint) == Enum.max_different_bits_neardups) {
        return true
      }
    }
    return false
  }

  def add(fingerprint : Long): Unit = {
    fg_DB += fingerprint
  }
  
  def add_to_print(f : Long, s : String){
     fg_MAP += (f -> s)
  }
  
  private val fg_DB: HashSet[Long] = new HashSet[Long]()
  private val fg_MAP : HashMap[Long, String] = new HashMap[Long, String]()
}