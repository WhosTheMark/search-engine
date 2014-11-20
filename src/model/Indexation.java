package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Indexation {

    private static final Logger LOGGER = Logger.getLogger(Indexation.class.getName());
    public static final String SEPARATOR_REGEXP = "[^A-Za-z0-9éàèùâêîôûëïüÿçœæ]+";
    private static final int WORD_MAX_LENGTH = 7;

    // To avoid instantiation
    private Indexation() {
    }

    /*
     * Takes a folder and index the files inside it.
     */
    public static void indexFiles(File stopWordsfile, File folder){

        File[] listOfFiles = folder.listFiles();

        if(listOfFiles == null) {
            LOGGER.log(Level.SEVERE, "Folder " + folder.getName() + "does not exists.");
            return;
        }

        Arrays.sort(listOfFiles);
        Set<String> stopWordsSet = Indexation.createStopWordsSet(stopWordsfile);

        LOGGER.log(Level.INFO, "Index process started.");

        int i = 0;

        for (File file : listOfFiles) {
            LOGGER.log(Level.INFO, "Indexing file: " + file.getName());
            createInverseFile(file, i++, stopWordsSet);
        }

        LOGGER.log(Level.INFO, "Index finished.");

    }

    /*
     * Create an inverse file of a document and store it in the database
     */
    private static void createInverseFile(File file, int documentId,
            Set<String> stopWordsSet) {

        LOGGER.log(Level.FINE, "Creating inverse file for document "
                + file.getName());

        Elements elems;

        try {
            elems = parse(file);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Could not parse file " + file.getName(),e);
            return;
        }

        InverseFile invFile = new InverseFile(documentId, file.getName(), stopWordsSet);

        // For each tag get the text and store it in the inverse file.
        for (Element e : elems) {

            String elemStr = e.ownText();
            String[] words = elemStr.split(SEPARATOR_REGEXP);
            invFile.addWord(words);
        }

        invFile.storeInDB();
    }

    /*
     * Modify a keyword to store it in the database.
     */
    public static String normalizeWord(String keyWord) {

        String lowerCaseWord = keyWord.toLowerCase();

        // Truncate string if it is too long
        if (lowerCaseWord.length() > WORD_MAX_LENGTH){
            lowerCaseWord = lowerCaseWord.substring(0,WORD_MAX_LENGTH);
        }

        return lowerCaseWord;
    }

    /*
     * Parse an HTML document using JSoup
     */
    private static Elements parse(File fileToParse) throws IOException {

        LOGGER.log(Level.FINE, "Using JSoup to parse " + fileToParse.getName());
        Document doc;

        doc = Jsoup.parse(fileToParse, "UTF-8", "");

        // Select all the tags from the HTML file
        return doc.select("*");
    }

    /*
     * Creates the set of words to not be included in the database
     */
    private static Set<String> createStopWordsSet(File file) {

        LOGGER.log(Level.FINE, "Building stop words set.");
        Scanner scanner;
        Set<String> set = new HashSet<String>();

        try {
            scanner = new Scanner(file);

        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not find stop list file", e);
            return set;
        }

        scanner.useDelimiter(SEPARATOR_REGEXP);

        // For each word in the file add it to the set
        while (scanner.hasNext()) {
            String word = scanner.next();
            String normalizedWord = normalizeWord(word);
            set.add(normalizedWord);
        }

        return set;
    }

}
