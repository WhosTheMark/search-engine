package model.indexation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Indexer {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String SEPARATOR_REGEXP = "[^A-Za-z0-9éàèùâêîôûëïüÿçœæ]+";
    private static final int WORD_MAX_LENGTH = 7;
    private static final int THREADS_NUM = 20;

    // To avoid instantiation
    private Indexer() {
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

    private static void indexFiles(File[] listOfFiles, Set<String> stopWordsSet) {

        LOGGER.entry(listOfFiles, stopWordsSet);
        LOGGER.info("Index process started.");

        Thread[] threads = initializeThreads(listOfFiles, stopWordsSet);
        waitForThreads(threads);

        LOGGER.info("Index finished.");
    }

    private static Thread[] initializeThreads(File[] listOfFiles,
            Set<String> stopWordsSet) {

        Thread[] threads = new Thread[THREADS_NUM];

        for(int i = 0; i < threads.length; ++i){
            threads[i] = new Thread(new IndexerRunnable(listOfFiles,stopWordsSet));
        }

        for(Thread thread : threads){
            thread.start();
        }

        return threads;
    }

    private static void waitForThreads(Thread[] threads) {

        try {

            for(Thread thread : threads){
                thread.join();
            }

        } catch (InterruptedException e) {
            LOGGER.error("Main thread was interrupted while waiting for other threads.",e);
        }
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
