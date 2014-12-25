package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.calculators.RelevanceCalculator;

/*
 * Searches relevant documents in the database.
 */
public class Searcher {

    private static final Logger LOGGER = LogManager.getLogger();
    private String resultFolder;

    // Implements strategy pattern to use different calculators
    protected RelevanceCalculator calculator;


    public Searcher(RelevanceCalculator calculator, String resultFolder) {
        this.calculator = calculator;
        this.resultFolder = resultFolder;
    }


    public List<RelevantDocument> executeQuery(String query) {

        LOGGER.entry(query);

        String[] keywords = query.split(Indexation.SEPARATOR_REGEXP);
        return executeQuery(keywords);
    }


    public List<RelevantDocument> executeQuery(String[] keywords) {

        LOGGER.info("Calculating relevant documents for the query: {}.",
                Arrays.toString(keywords));

        for (int i = 0; i < keywords.length; ++i) {
            List<RelevantDocument> relevantDocs = getRelevantDocsOfKeyword(keywords[i]);
            calculator.addDocuments(relevantDocs);
        }

        return LOGGER.exit(sortRelevantDocs());

    }

    protected List<RelevantDocument> sortRelevantDocs() {
        List<RelevantDocument> relvDocs = calculator.calculateRelevantDocs();
        Collections.sort(relvDocs);
        return relvDocs;
    }


    protected List<RelevantDocument> getRelevantDocsOfKeyword(String keyword) {

        LOGGER.debug("Calculating relevant documents for the keyword {}.",
                keyword);

        String normalizedKeyword = Indexation.normalizeWord(keyword);

        return RelevantDocument.getRelevantDocs(normalizedKeyword);
    }


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

    // Get the relevant documents of a query and store them in a file.
    private void executeQueries(Scanner scanner) {

        for(int i = 1; scanner.hasNextLine(); ++i){

            String query = scanner.nextLine();
            List<RelevantDocument> docs = executeQuery(query);
            writeResultToFile(docs,i);
        }
    }

    /*
     * Write the result of a query in a file.
     */
    private void writeResultToFile(List<RelevantDocument> docs, int queryID){

        LOGGER.debug("Writing the result of query number {}.", queryID);
        PrintWriter writer = null;

        // Opens a file or creates it to store the results.
        try {

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

    /*
     * Create the folder to store the results if it does not exists.
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
