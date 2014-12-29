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

import model.database.DAO.InverseFileDAO;

public class IndexerRunnable implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static AtomicInteger counter = new AtomicInteger(0);
    private File[] listOfFiles;
    private Set<String> stopWordsSet;

    public IndexerRunnable(File[] listOfFiles, Set<String> stopWordsSet){
        this.listOfFiles = listOfFiles;
        this.stopWordsSet = stopWordsSet;
    }

    @Override
    public void run() {

        InverseFileDAO invDAO = new InverseFileDAO();

        int documentId = counter.getAndIncrement();

        while (documentId < listOfFiles.length) {
            File file = listOfFiles[documentId];
            LOGGER.info("Indexing file: {}.", file.getName());
            InverseFile invFile = indexFile(file, documentId++, stopWordsSet);
            invDAO.store(invFile);
            documentId = counter.getAndIncrement();
        }

        invDAO.finalize();
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
            String[] words = elemStr.split(Indexer.SEPARATOR_REGEXP);
            addWordsToInv(words,invFile, stopWordsSet);
        }

        LOGGER.exit();
    }

    private static void addWordsToInv(String[] words, InverseFile invFile,
            Set<String> stopWordsSet) {

        LOGGER.entry(words,invFile,stopWordsSet);

        for (String word : words) {
            String treatedWord = Indexer.normalizeWord(word);
            if (!treatedWord.isEmpty() && !stopWordsSet.contains(treatedWord)) {
                invFile.addWord(treatedWord);
            }
        }

        LOGGER.exit();
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

}
