import java.io.File;
import java.io.FileNotFoundException;
import java.util.TreeSet;

import controlleur.Indexation;


public class Main {

	public static void main(String[] args) throws FileNotFoundException {
		
		File folder = new File("extra/CORPUS");
		File[] listOfFiles = folder.listFiles();
		TreeSet<String> emptyWordsSet = Indexation.createEmptyWordsSet();
		
		System.out.println("Index process started.");
		int i = 0;
		
		for (File file: listOfFiles) {
			System.out.println("Indexing file: " + file.getName());
			Indexation.createInverseFile(file, i++, emptyWordsSet);
		}
		
		System.out.println("Index finished.");

	}
	
}
