package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.calculators.RelevanceCalculator;

/*
 * Searches relevant documents in the database.
 */
public class Search {

    private static final Logger LOGGER = Logger.getLogger(Search.class.getName());
    private String resultFolder;

    // Implements strategy pattern to use different calculators
    private RelevanceCalculator calculator;


    public Search(RelevanceCalculator calculator, String resultFolder) {
        this.calculator = calculator;
        this.resultFolder = resultFolder;
    }


    public List<RelevantDocument> getRelevantDocsOfQuery(String query) {

        LOGGER.log(Level.INFO, "Calculating relevant documents for the query: "
                + query + ".");

        String[] keywords = query.split(Indexation.SEPARATOR_REGEXP);

        for (String keyword : keywords) {
            getRelevantDocsOfKeyword(keyword);
        }

        List<RelevantDocument> relvDocs = calculator.finalizeCalcs();
        Collections.sort(relvDocs);

        return relvDocs;
    }


    private void getRelevantDocsOfKeyword(String keyword) {

        LOGGER.log(Level.FINER, "Calculating relevant documents for the keyword "
                + keyword + ".");

        String normalizedKeyword = Indexation.normalizeWord(keyword);

        List<RelevantDocument> relevantDocs =
                RelevantDocument.getRelevantDocs(normalizedKeyword);
        calculator.calculateRelevance(relevantDocs);
    }


    public void searchQueriesFromFile(File file){

        Scanner scanner = null;

        try {
            scanner = new Scanner (file);
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "File " + file.getName() + "not found.", e);
            return;
        }

        if (!resultFolderExists()){
            return;
        }

        executeQueries(scanner);

        LOGGER.log(Level.INFO, "Search finished.");
    }

    // Get the relevant documents of a query and store them in a file.
    private void executeQueries(Scanner scanner) {

        for(int i = 1; scanner.hasNextLine(); ++i){

            String query = scanner.nextLine();
            List<RelevantDocument> docs = getRelevantDocsOfQuery(query);
            writeResultToFile(docs,i);
        }
    }

    /*
     * Create the folder to store the results if it does not exists.
     */
    private boolean resultFolderExists() {

        File folder = new File(resultFolder);

        if (!folder.exists()) {
            LOGGER.log(Level.CONFIG, "Creating results foder.");
            folder.mkdir();
        }

        if (!folder.exists()) {
            LOGGER.log(Level.SEVERE, "Could not create folder "
                    + folder.getPath() + " to store results.");
            return false;
        }

        return true;
    }

    /*
     * Write the result of a query in a file.
     */
    private void writeResultToFile(List<RelevantDocument> docs, int queryID){

        LOGGER.log(Level.FINE, "Writing the result of query number "+ queryID + ".");
        PrintWriter writer = null;

        // Opens a file or creates it to store the results.
        try {

            writer = new PrintWriter(resultFolder + "/qrelQ" + queryID + ".txt");

        } catch (FileNotFoundException e) {

            LOGGER.log(Level.SEVERE, "File qrelQ" + queryID +
                    ".txt could not be created", e);
            return;
        }

        for(RelevantDocument doc : docs){
            writer.println(doc.getDocumentName());
        }

        writer.close();
    }

}
