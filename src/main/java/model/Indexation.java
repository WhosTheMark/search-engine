package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class Indexation {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String SEPARATOR_REGEXP = "[^A-Za-z0-9éàèùâêîôûëïüÿçœæ]+";
    private static final int WORD_MAX_LENGTH = 7;

    // To avoid instantiation
    private Indexation() {
    }

    /*
     * Takes a folder and index the files inside it.
     */
    public static void startIndexation(File stopWordsfile, File folder){

        LOGGER.entry(stopWordsfile,folder);

        File[] listOfFiles = folder.listFiles();

        if(listOfFiles == null) {
            LOGGER.error("Folder {} does not exists.", folder.getName());
            return;
        }

        Arrays.sort(listOfFiles);
        Set<String> stopWordsSet = createStopWordsSet(stopWordsfile);

        indexFiles(listOfFiles, stopWordsSet);

        LOGGER.exit();
    }

    private static void indexFiles(File[] listOfFiles,  Set<String> stopWordsSet) {

        LOGGER.entry(listOfFiles, stopWordsSet);
        LOGGER.info("Index process started.");

        int documentId = 0;

        for (File file : listOfFiles) {
            LOGGER.info("Indexing file: {}.", file.getName());
            InverseFile invFile = indexFile(file, documentId++, stopWordsSet);
            invFile.storeInDB();
        }

        LOGGER.info("Index finished.");
    }

    /*
     * Create an inverse file of a document and store it in the database
     */
    private static InverseFile indexFile(File file, int documentId,
            Set<String> stopWordsSet) {

        LOGGER.entry(file, documentId, stopWordsSet);
        LOGGER.debug("Creating inverse file for document {}.", file.getName());

        Elements elems = parse(file);
        InverseFile invFile = new InverseFile(documentId, file.getName());
        addElems(elems, invFile, stopWordsSet);

        LOGGER.exit(invFile);
        return invFile;
    }

    /*
     * Takes the text in the tags and adds them to the inverse file.
     */
    private static void addElems(Elements elems, InverseFile invFile,
            Set<String> stopWordsSet) {

        LOGGER.entry(elems, invFile, stopWordsSet);

        for (Element e : elems) {

            String elemStr = e.ownText();
            String[] words = elemStr.split(SEPARATOR_REGEXP);
            addWordsToInv(words,invFile, stopWordsSet);
        }

        LOGGER.exit();
    }

    private static void addWordsToInv(String[] words, InverseFile invFile,
            Set<String> stopWordsSet) {

        LOGGER.entry(words,invFile,stopWordsSet);

        for (String word : words) {
            String treatedWord = normalizeWord(word);
            if (!treatedWord.isEmpty() && !stopWordsSet.contains(treatedWord)) {
                invFile.addWord(treatedWord);
            }
        }

        LOGGER.exit();
    }

    /*
     * Modify a keyword to store it in the database.
     */
    public static String normalizeWord(String keyword) {

        LOGGER.entry(keyword);

        String lowerCaseWord = keyword.toLowerCase();

        // Truncate string if it is too long
        if (lowerCaseWord.length() > WORD_MAX_LENGTH){
            lowerCaseWord = lowerCaseWord.substring(0,WORD_MAX_LENGTH);
        }

        return LOGGER.exit(lowerCaseWord);
    }

    /*
     * Parse an HTML document using JSoup
     */
    private static Elements parse(File fileToParse) {

        LOGGER.debug("Using JSoup to parse {}.", fileToParse.getName());
        Document doc;

        try {
            doc = Jsoup.parse(fileToParse, "UTF-8", "");
        } catch (IOException e) {
            LOGGER.error("Could not parse file " + fileToParse.getName() + ".",e);
            return new Elements();
        }

        // Select all the tags from the HTML file
        return doc.select("*");
    }

    /*
     * Creates the set of words to not be included in the database
     */
    private static Set<String> createStopWordsSet(File file) {

        LOGGER.debug("Building stop words set using file {}.", file.getName());
        Scanner scanner;
        Set<String> set = new HashSet<String>();

        try {
            scanner = new Scanner(file);

        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find stop list file.", e);
            return set;
        }

        scanner.useDelimiter(SEPARATOR_REGEXP);
        addWordsToSet(scanner, set);

        LOGGER.exit(set);
        return set;
    }

    private static void addWordsToSet(Scanner scanner, Set<String> set) {

        LOGGER.entry(scanner,set);

        while (scanner.hasNext()) {
            String word = scanner.next();
            String normalizedWord = normalizeWord(word);
            set.add(normalizedWord);
        }

        LOGGER.exit();
    }
}
