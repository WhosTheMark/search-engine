package controlleur;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import model.DBDriver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Indexation {
	
	private static final String separatorRegExpr = "( |,|;|\\.|:|!|\\?)+";
	
	public static void createInverseFile(File file, int document){
		
		String[] words = parse(file);
		HashMap<String, Integer> inverseFile = new HashMap<String, Integer>();
		
		for (String word : words){
			
			int frequency = 1;
			
			if(inverseFile.containsKey(word))
				frequency = inverseFile.get(word);
			
			inverseFile.put(word, frequency);		
		}
		
		DBDriver.storeInverseFile(inverseFile, document);
		
	}
	
	private static String[] parse(File fileToParse){
		
		Document doc;
		
		try {
			doc = Jsoup.parse(fileToParse, "UTF-8", "");
			
		} catch (IOException e) {
			e.printStackTrace();
			return new String[0];
		}
		
		String text = doc.text();
		return text.split(separatorRegExpr);		
	}

}
