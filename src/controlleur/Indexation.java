package controlleur;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Indexation {
	
	public static HashMap<String, Integer> parse(File fileToParse){
		
		HashMap<String, Integer> reverseFile = new HashMap<String, Integer>();
		
		try {
			Document doc = Jsoup.parse(fileToParse, "UTF-8", "");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return reverseFile;
	}

}
