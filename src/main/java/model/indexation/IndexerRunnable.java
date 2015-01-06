package model.indexation;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import model.database.dao.InverseFileDAO;

/**
 * Class with the operations used by each thread in the Indexation process.
 * This class will take HTML files, parse them, add the words in each tag in a
 * InverseFile object and finally store each object in the database.
 */
class IndexerRunnable implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();

    // Counter used by all the threads to take elements from the array of files.
    private static AtomicInteger counter = new AtomicInteger(0);
    private File[] filesToIndex;

    // Set of words to be ignored.
    private Set<String> stopWordsSet;

    /**
     * Creates this object using a list of files to parse and a set of the words
     * to be ignored.
     * @param filesToIndex an array of files to parse.
     * @param stopWordsSet the words to be ignored.
     */
    IndexerRunnable(File[] filesToIndex, Set<String> stopWordsSet){
        this.filesToIndex = filesToIndex;
        this.stopWordsSet = stopWordsSet;
    }

    @Override
    public void run() {
        indexFiles();
    }

    /**
     * Indexes concurrently the HTML files.
     */
    private void indexFiles() {

        InverseFileDAO invDAO = new InverseFileDAO();

        int documentId = counter.getAndIncrement();

        while (documentId < filesToIndex.length) {
            File file = filesToIndex[documentId];
            LOGGER.info("Indexing file: {}.", file.getName());
            InverseFile invFile = indexFile(file, documentId, stopWordsSet);
            invDAO.store(invFile);
            documentId = counter.getAndIncrement();
        }

        invDAO.closeConnection();
    }

    /**
     * Indexes a single file.
     * @param file reference to the file to index.
     * @param documentId ID of the document.
     * @param stopWordsSet set of the words to be ignored.
     * @return an InverseFile containing all the words found.
     * @see InverseFile
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

    /**
     * Takes the text in the tags and adds them to the inverse file.
     * @param elems the list of HTML tags.
     * @param invFile the Inverse file where the words will be added.
     * @param stopWordsSet set of the words to be ignored.
     * @see InverseFile
     */
    private static void addElems(Elements elems, InverseFile invFile,
            Set<String> stopWordsSet) {

        LOGGER.entry(elems, invFile, stopWordsSet);

        for (Element e : elems) {

            String elemStr = e.ownText();
            String[] words = WordNormalizer.split(elemStr);
            addWordsToInv(words,invFile, stopWordsSet);
        }

        LOGGER.exit();
    }

    /**
     * Adds an array of words to the Inverse File.
     * @param words array of words to be added.
     * @param invFile invFile the Inverse file where the words will be added.
     * @param stopWordsSet set of the words to be ignored.
     * @see InverseFile
     */
    private static void addWordsToInv(String[] words, InverseFile invFile,
            Set<String> stopWordsSet) {

        LOGGER.entry(words,invFile,stopWordsSet);

        for (String word : words) {
            String treatedWord = WordNormalizer.normalize(word);
            if (!treatedWord.isEmpty() && !stopWordsSet.contains(treatedWord)) {
                invFile.addWord(treatedWord);
            }
        }

        LOGGER.exit();
    }

    /**
     * Parse an HTML document using JSoup.
     * @param fileToParse the file to parse.
     * @return an object containing the tags in the file.
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

}
