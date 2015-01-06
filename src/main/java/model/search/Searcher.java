package model.search;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.database.dao.RelevantDocumentDAO;
import model.indexation.WordNormalizer;
import model.search.calculators.RelevanceCalculator;

/**
 * Class that searches relevant documents in the database.
 */
public class Searcher {

    private static final Logger LOGGER = LogManager.getLogger();
    private final String resultFolder;

    // Implements strategy pattern to use different calculators
    protected RelevanceCalculator calculator;
    private RelevantDocumentDAO relvDocDAO;

    /**
     * Creates a searcher object.
     * @param calculator
     * @param resultFolder
     * @see RelevanceCalculator
     */
    public Searcher(RelevanceCalculator calculator, String resultFolder) {
        this.calculator = calculator;
        this.resultFolder = resultFolder;
    }

    /**
     * Executes a single query in the form of a String.
     * @param query the query to execute.
     * @return the list of relevant documents found already sorted.
     * @see RelevantDocument
     */
    public List<RelevantDocument> executeSingleQuery(String query) {

        relvDocDAO = new RelevantDocumentDAO();
        List<RelevantDocument> list = executeQuery(query);
        relvDocDAO.closeConnection();
        return list;
    }

    /**
     * Executes a single query in the form of a String but does not establish
     * the connection to the database.
     * @param query the query containing keywords.
     * @return the list of relevant documents found already sorted.
     */
    protected List<RelevantDocument> executeQuery(String query) {

        LOGGER.entry(query);

        String[] keywords = WordNormalizer.split(query);
        return executeQuery(keywords);
    }

    /**
     * Executes a query already split in keywords.
     * @param keywords the keywords of the query.
     * @return the list of relevant documents found already sorted.
     */
    private List<RelevantDocument> executeQuery(String[] keywords) {

        LOGGER.info("Calculating relevant documents for the query: {}.",
                Arrays.toString(keywords));

        for (int i = 0; i < keywords.length; ++i) {
            List<RelevantDocument> relevantDocs = getRelevantDocsOfKeyword(keywords[i]);
            calculator.addDocuments(relevantDocs);
        }

        return LOGGER.exit(sortRelevantDocs());

    }

    /**
     * Sorts the documents found.
     * @return the list of relevant documents found.
     */
    protected List<RelevantDocument> sortRelevantDocs() {
        List<RelevantDocument> relvDocs = calculator.calculateRelevantDocs();
        Collections.sort(relvDocs);
        return relvDocs;
    }

    /**
     * Gets the list of relevant documents of a keyword from the database.
     * @param keyword the word to search.
     * @return the list of relevant documents found.
     */
    protected List<RelevantDocument> getRelevantDocsOfKeyword(String keyword) {

        LOGGER.debug("Calculating relevant documents for the keyword {}.",
                keyword);

        String normalizedKeyword = WordNormalizer.normalize(keyword);

        return relvDocDAO.getRelevantDocs(normalizedKeyword);
    }

    /**
     * Executes several queries that are stored in a file, one per line.
     * @param file the file where the queries can be found.
     */
    public void executeQueriesFromFile(File file){

        Scanner scanner = null;

        try {
            scanner = new Scanner (file);
        } catch (FileNotFoundException e) {
            LOGGER.fatal("File " + file.getName() + "not found.", e);
            return;
        }

        if (!resultFolderExists()){
            if (scanner != null) {
                scanner.close();
            }
            return;
        }

        executeQueries(scanner);

        LOGGER.info("Search finished.");
    }

    /**
     * Executes the queries in a file using its text scanner.
     * @param scanner the text scanner of the file.
     */
    private void executeQueries(Scanner scanner) {

        relvDocDAO = new RelevantDocumentDAO();

        for(int i = 1; scanner.hasNextLine(); ++i){

            String query = scanner.nextLine();
            List<RelevantDocument> docs = executeQuery(query);
            writeResultToFile(docs,i);
        }

        relvDocDAO.closeConnection();
    }

    /**
     * Writes the result of a query in a file.
     * @param docs the documents to write in the file.
     * @param queryID the number of the query. It is used for reading the result
     * easily.
     */
    private void writeResultToFile(List<RelevantDocument> docs, int queryID){

        LOGGER.debug("Writing the result of query number {}.", queryID);
        PrintWriter writer = null;

        try {

         // Opens a file or creates it to store the results.
            writer = new PrintWriter(resultFolder + "/qrelQ" + queryID + ".txt");

        } catch (FileNotFoundException e) {

            LOGGER.error("File qrelQ" + queryID + ".txt could not be created", e);
            return;
        }

        for(RelevantDocument doc : docs){
            writer.println(doc.getDocumentName());
        }

        writer.close();
    }

    /**
     * Creates the folder to store the results if it does not exists.
     * @return true if the folder exists or if it was successfully created.
     */
    private boolean resultFolderExists() {

        File folder = new File(resultFolder);

        if (!folder.exists()) {
            LOGGER.debug("Creating results folder {}.", folder.getAbsolutePath());
            folder.mkdir();
        }

        if (!folder.exists()) {
            LOGGER.fatal("Could not create folder {} to store results.",
                    folder.getAbsolutePath());
            return false;
        }

        return true;
    }
}
