package langrecognition

import java.io.File
import scala.collection.mutable
import scala.io.Source
import scala.math.log

/** Naive 100 lines language recognition tool
 *  
 *  Provide as command line arguments
 *  	1) An English corpus
 *   	2) A German corpus
 *    	3) A test file with one sentence per line 
 * 
 *  DISCLAIMER: This is partly a very naive text processing pipeline and far from best practice
 *  			Runs - depending on your machine - for a few minutes
 */
object LanguageRecognizer {
	// Some types
	type Model = Map[String, Double]
	type Dictionary = Set[String]
	
	def main(args : Array[String]) = {
		if(args.size != 3)
			throw new Exception("Provide three files")
		
		val (pathEN, pathDE, pathTest) = (new File(args(0)), new File(args(1)), new File(args(2)))
		
		// One dictionary per language
		val dictEN = gatherDictionary(Seq(pathEN, pathTest))
		val dictDE = gatherDictionary(Seq(pathDE, pathTest))
		// println(s"Dictionary sizes EN: ${dictEN.size} DE: ${dictDE.size}\nLearning models...")
		
		println("ping")
		val modelEN = estimateMLEProbabilitiesFromFile(pathEN, dictEN)
		println("pong")
		val modelDE = estimateMLEProbabilitiesFromFile(pathDE, dictDE)
		println("...models learned")
		
		// Predict the language of the sentences
		val testSentences = Source.fromFile(pathTest).getLines.toList
		var pageScoreEN = 0
		var pageScoreDE = 0
		for(sentence <- testSentences){
			// Compute likelihood scores under both models
			val scoreEN = scoreSentence(sentence, dictEN, modelEN)
			val scoreDE = scoreSentence(sentence, dictDE, modelDE)
			
			val language = if(scoreEN > scoreDE) "English" else "German"
			
			if(scoreEN > scoreDE) pageScoreEN += 1 else pageScoreDE += 1
			// println(s"This is a $language sentence ($scoreEN vs. $scoreDE): $sentence")
		}
		val language: String = if(pageScoreEN > pageScoreDE) "English" else "German"
		println(s"This is a $language sentence")
	}

	/** Simply includes all tokens in all documents in the dictionary
	 */
	def gatherDictionary(files : Seq[File]) : Set[String] = {
		val dict = mutable.Set[String]()
		
		for{file <- files
			line <- Source.fromFile(file).getLines()
			token <- tokenize(line)}
			dict += token

		dict.toSet
	}
	
	/** Simple add-one smoothed counts for all words in the dictionary
	 */
	def estimateMLEProbabilitiesFromFile(file : File, dict : Dictionary) : Model = {
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
		
		// Corpus size
		val nrTokens = globalCounts.values.sum
		
		// Normalize
		globalCounts.mapValues(count => (count.toDouble + 1) / (nrTokens + dict.size)).toMap
	}
	
	/** Score a sentence under a certain model. Exclude tokens not in the dictionary.
	 */
	def scoreSentence(sentence : String, dict : Dictionary, model : Model) : Double = {
		tokenize(sentence).map(tok => log(model(tok))).sum
	}
	
	/** Simple tokenization
	 */ 
	def tokenize(text : String)  = text.split("\\W+")
}