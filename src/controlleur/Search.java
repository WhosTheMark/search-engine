package controlleur;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.DBDriver;
import model.RelevantDocument;
import model.calculators.RelevanceCalculator;

/*
 * Searches relevant documents in the database.
 */
public class Search {

    private static final Logger LOGGER = Logger.getLogger(Search.class.getName());

    // Implements strategy pattern
    private RelevanceCalculator calculator;


    public Search(RelevanceCalculator calculator) {
        this.calculator = calculator;
    }

    /*
     * Get the relevant documents of a request
     */
    public List<RelevantDocument> getRelevantDocs(String[] keywords) {

        LOGGER.log(Level.FINE, "Calculating relevant documents.");

        for (String keyword : keywords) {

            List<RelevantDocument> relevantDocs = DBDriver.getRelevantDocs(keyword);
            calculator.calculateRelevance(relevantDocs);

        }

        List<RelevantDocument> relvDocs = calculator.finalizeCalcs();
        Collections.sort(relvDocs);

        return relvDocs;
    }

}
