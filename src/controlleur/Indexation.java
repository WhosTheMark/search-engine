package controlleur;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.DBDriver;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Indexation {

    private static final Logger LOGGER = Logger.getLogger(Indexation.class.getName());
    private static final String SEPARATOR_REGEXP = "[^A-Za-z0-9éàèùâêîôûëïüÿçœæ]+";

    // To avoid instantiation
    private Indexation() {
    }

    public static void indexFiles(File emptyWordsfile, File folder){

        File[] listOfFiles = folder.listFiles();
        Set<String> emptyWordsSet = Indexation
                .createEmptyWordsSet(emptyWordsfile);

        LOGGER.log(Level.INFO, "Index process started.");

        int i = 0;

        for (File file : listOfFiles) {
            LOGGER.log(Level.INFO, "Indexing file: " + file.getName());
            createInverseFile(file, i++, emptyWordsSet);
        }

        LOGGER.log(Level.INFO, "Index finished.");

    }

    /*
     * Create an inverse file of a document and store it in the database
     */
    private static void createInverseFile(File file, int documentId,
            Set<String> emptyWordsSet) {

        LOGGER.log(Level.FINE, "Creating inverse file for document "
                + file.getName());

        DBDriver.storeDocument(documentId, file);
        Elements elems;

        try {
            elems = parse(file);
        } catch (IOException e1) {
            LOGGER.log(Level.SEVERE, "Could not parse file " + file.getName());
            return;
        }

        Map<String, Integer> inverseFile = new HashMap<String, Integer>();

        // For each tag get the text and store it in the inverse file.
        for (Element e : elems) {

            String elemStr = e.ownText();
            String[] words = elemStr.split(SEPARATOR_REGEXP);
            calculateFrequencies(words, inverseFile, emptyWordsSet);

        }

        DBDriver.storeInverseFile(inverseFile, documentId);
    }

    /*
     * Calculates the frequency of each word in a HTML tag and stores it in
     * the inverse file.
     */
    private static void calculateFrequencies(String[] words,
            Map<String, Integer> inverseFile, Set<String> emptyWordsSet) {

        for (String word : words) {

            LOGGER.log(Level.FINEST, "Calculating frequency for word " + word);

            // If the word is not an empty string or an empty word we store it
            if (!word.isEmpty() && !emptyWordsSet.contains(word)) {

                String lowerCaseWord = word.toLowerCase();
                int frequency = 1;

                /*
                 * If the word is not already in the inverse file
                 * then its frequency is 1, else is the frequency it had + 1
                 */
                if (inverseFile.containsKey(lowerCaseWord)) {
                    frequency = 1 + inverseFile.get(lowerCaseWord);
                }

                inverseFile.put(lowerCaseWord, frequency);
            }
        }

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
    private static Set<String> createEmptyWordsSet(File file) {

        LOGGER.log(Level.FINE, "Building empty words set.");
        Scanner scanner;
        Set<String> set = new TreeSet<String>();

        try {
            scanner = new Scanner(file);

        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Could not find empty word file", e);
            return set;
        }

        scanner.useDelimiter(SEPARATOR_REGEXP);

        // For each word in the file add it to the set
        while (scanner.hasNext()) {
            String word = scanner.next();
            set.add(word);
        }

        return set;
    }

}
