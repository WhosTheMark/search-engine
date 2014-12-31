package model.indexation;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to index files and store the data in a database. It uses several threads
 * to accelerate the indexing process.
 * @see IndexerRunnable
 */
public class Indexer {

    private static final Logger LOGGER = LogManager.getLogger();

    // Number of threads to be used.
    private static final int THREADS_NUM = 10;

    // To avoid instantiation
    private Indexer() {}

    /**
     * Indexes the files inside a given folder and a file with the list of words
     * to ignore.
     * @param folder the folder where the documents can be found.
     * @param stopWordsfile the file with the words to be ignored.
     */
    public static void startIndexation(File folder, File stopWordsfile){

        LOGGER.entry(stopWordsfile,folder);

        File[] listOfFiles = folder.listFiles();

        if(listOfFiles == null) {
            LOGGER.error("Folder {} does not exists.", folder.getName());
            return;
        }

        Arrays.sort(listOfFiles);
        Set<String> stopWordsSet = StopWordSetBuilder.createStopWordsSet(stopWordsfile);

        indexFiles(listOfFiles, stopWordsSet);

        LOGGER.exit();
    }

    /**
     * Indexes the files using the set of words to be ignored.
     * @param listOfFiles array with the files to be indexed.
     * @param stopWordsSet set of words to be ignored.
     */
    private static void indexFiles(File[] listOfFiles, Set<String> stopWordsSet) {

        LOGGER.entry(listOfFiles, stopWordsSet);
        LOGGER.info("Index process started.");

        Thread[] threads = initializeThreads(listOfFiles, stopWordsSet);
        waitForThreads(threads);

        LOGGER.info("Index finished.");
    }

    /**
     * Starts the threads that will index the files.
     * @param listOfFiles array with the files to be indexed.
     * @param stopWordsSet set of words to be ignored.
     * @return an array with the threads created.
     */
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

    /**
     * Waits for all the threads to finish their job.
     * @param threads the threads to wait.
     */
    private static void waitForThreads(Thread[] threads) {

        try {

            for(Thread thread : threads){
                thread.join();
            }

        } catch (InterruptedException e) {
            LOGGER.error("Main thread was interrupted while waiting for other threads.",e);
        }
    }
}
