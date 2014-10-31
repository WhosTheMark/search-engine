import java.io.File;

import controlleur.Indexation;


public class Main {

	public static void main(String[] args) {
		File folder = new File("/home/marcos/workspace/search-engine/extra/CORPUS");
		File[] listOfFiles = folder.listFiles();
		
		System.out.println("Index process started.");
		int i = 0;
		
		for (File file: listOfFiles) {
			System.out.println("Indexing file: " + file.getName());
			Indexation.createInverseFile(file, i++);
		}
		
		System.out.println("Index finished.");

	}
	
}
