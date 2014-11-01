package controlleur;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeSet;
import model.DBDriver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Indexation {
	
	private static final String separatorRegExpr = "[^A-Za-z0-9éàèùâêîôûëïüÿçœæ]+";
	
	public static void createInverseFile(File file, int document, TreeSet<String> emptyWordsSet){
		
		//Insert a document in the database
		DBDriver.storeDocument(document,file);
		Elements elems = parse(file);
		HashMap<String, Integer> inverseFile = new HashMap<String, Integer>();
		
		//For each tag get the text and store it in the inverse file.
		for (Element e : elems){
			
			String elemStr = e.ownText();
			String[] words = elemStr.split(separatorRegExpr);
			calculateFrequencies(words, inverseFile, emptyWordsSet);

		}
		
		//Store the indexes in the database
		DBDriver.storeInverseFile(inverseFile, document);
	}
	
	private static void calculateFrequencies(String[] words, 
			HashMap<String, Integer> inverseFile, TreeSet<String> emptyWordsSet){
		
		for(String word: words) {
			
			//If the word is not an empty string or an empty word we store it
			if (!word.isEmpty() && !emptyWordsSet.contains(word)){
				
				//All the words are lower case'd
				String lowerCaseWord = word.toLowerCase();
				int frequency = 1;
				
				/*
				 * If the word is not already in the inverse file the 
				 * inverse file then its frequency is 1, else is the
				 * frequency it had +1
				 */
				if(inverseFile.containsKey(lowerCaseWord))
					frequency = 1 + inverseFile.get(lowerCaseWord);
					
				inverseFile.put(lowerCaseWord, frequency);
			}
		}
		
	}
	
	private static Elements parse(File fileToParse){
		
		Document doc;
		
		try {
			doc = Jsoup.parse(fileToParse, "UTF-8", "");
			
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		//Select all the tags from the HTML file
		Elements elems = doc.select("*");
		return elems;		
	}
	
	public static TreeSet<String> createEmptyWordsSet(File file){
		
		Scanner scanner;
		
		//Try to open the file
		try {
			scanner = new Scanner(file);
		
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			return null;
		}
		
		scanner.useDelimiter(separatorRegExpr);
		TreeSet<String> set = new TreeSet<String>();
		
		//For each word in the file add it to the tree
		while (scanner.hasNext()){
			String word = scanner.next();
			set.add(word);
		}
		
		return set;
	}

}
