package langIdentification

import java.io.File
import scala.collection.mutable
import scala.io.Source
import scala.math.log

/** identify document in English or in German, using character n-gram
 *  IR exercise 2
 *  
 *  Provide as command line arguments
 *  	1) An English corpus
 *   	2) A German corpus
 *    	3) A test file with one sentence per line 
 * 
 *  DISCLAIMER: This is partly a very naive text processing pipeline and far from best practice
 *  			Runs - depending on your machine - for a few minutes
 */
object langIdentifier {
	
	def main(args : Array[String]) = {
		if(args.size != 3)
			throw new Exception("Provide three files")
		
		val (pathEN, pathDE, pathTest) = (new File(args(0)), new File(args(1)), new File(args(2)))
		
		val charaterLen = 3
		println("start learning dictEN")
		val dictEN: Map[String, Int] = ngrams(pathEN, charaterLen)
		println("finished dictEN, start learning dictDE")
		val dictDE: Map[String, Int] = ngrams(pathDE, charaterLen)
		println("finidshed dictDE")

		// val testFile: String = Source.fromFile(pathTest).mkString.replaceAll("[^A-Za-z ]", "").toLowerCase
		val scoreEN: Double = scoreText(pathTest, dictEN, charaterLen)
		val scoreDE: Double = scoreText(pathTest, dictDE, charaterLen)
		
		val result: String = if(scoreEN > scoreDE) "English" else "German"
		println(s"This is a $result text, with EN score: $scoreEN and DE score: $scoreDE")			
	}

	def scoreText(file: File, model: Map[String, Int], n: Int):Double = {
		val lineIter = Source.fromFile(file).getLines()
		var score: Double = 0
		val nrTokens: Double = model.values.sum
		for(line <- lineIter){
			val text: String = line.replaceAll("[^A-Za-z ]","").toLowerCase
			if(n>1 && text.length>=n) 
				score = score + text.sliding(n).toArray.map(tok => log( (model.getOrElse(tok,0)+1) / (nrTokens+1))).sum
		}
		score
		// text.sliding(n).toArray.map(tok => log( (model.getOrElse(tok,0)+1) / (nrTokens+1))).sum
		
	}

	def ngrams(file: File, n: Int): Map[String, Int] = {
		// val text: String = Source.fromFile(file).mkString.replaceAll("[^A-Za-z ]", "").toLowerCase
		val result = mutable.Map[String, Int]()
		val lineIter = Source.fromFile(file).getLines()
		for(line <- lineIter){
			val text: String = line.replaceAll("[^A-Za-z ]", "").toLowerCase
			if(n>=1 && text.length>=n){		
				for(ngram <- text.sliding(n))
					result(ngram) = result.getOrElse(ngram,0) + 1
			}
		}
		result.toMap
	}
}