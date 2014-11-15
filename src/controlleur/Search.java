package controlleur;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.DBDriver;
import model.RelevantDocument;
import model.calculators.InnerProductCalculator;
import model.calculators.RelevanceCalculator;

/*
 * Searches relevant documents in the database.
 */
public class Search {

    private static final Logger LOGGER = Logger.getLogger(Search.class.getName());

    // Implements strategy pattern
    private static final RelevanceCalculator CALCULATOR = new InnerProductCalculator();

    // To avoid instantiation
    private Search() {
    }

    /*
     * Get the relevant documents of a request
     */
    public static List<RelevantDocument> getRelevantDocs(String[] keywords) {

        LOGGER.log(Level.FINE, "Calculating relevant documents.");

        //List that accumulates relevant documents 
        List<RelevantDocument> docsAcc = new ArrayList<RelevantDocument>();

        for (String keyword : keywords) {

            List<RelevantDocument> relevantDocs = DBDriver.getRelevantDocs(keyword);
            docsAcc = CALCULATOR.calculateRelevance(docsAcc, relevantDocs);

        }

        Collections.sort(docsAcc);

        return docsAcc;
    }

}
