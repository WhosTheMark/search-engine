package model.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.indexation.WordNormalizer;
import model.sparql.SparqlDAO;

/**
 * Class that takes a query and will look in a SPARQL database related words
 * to enhance the original query. At the moment it only looks for synonymous.
 */
class QueryEnhancer {

    private static final Logger LOGGER = LogManager.getLogger();
    private SparqlDAO sparqlDAO;
    private static final String SEPARATOR_REGEXP = ",";

    /**
     * Creates a new enhancer and connects to the SPARQL database.
     */
    QueryEnhancer(){
        sparqlDAO =  new SparqlDAO();
    }

    /**
     * Takes a query and looks for related words in the SPARQL database.
     * @param query the query to enhance.
     * @return the list of words found.
     */
    List<String> enhanceQuery(String query){

        LOGGER.info("Procesing original query: {}.", query);

        String[] terms = query.split(SEPARATOR_REGEXP);
        return enhanceQuery(terms);
    }

    /**
     * Takes an array of strings and looks for related words in the SPARQL database.
     * @param terms array of strings
     * @return the list of words found.
     */
    List<String> enhanceQuery(String[] terms){

        LOGGER.entry(Arrays.toString(terms));

        List<String> list = new ArrayList<String>();

        for (String term : terms){
            String trimmedTerm = term.trim();
            List<String> labels = sparqlDAO.getOtherLabels(trimmedTerm);

            addLabelsToList(labels, list);
        }

        LOGGER.debug("Found {} word(s) to enhance query: {}.", list.size(), list);
        return list;
    }

    /**
     * Adds the synonymous to the list.
     * @param labels the list of synonymous found.
     * @param list the list where the labels will be added.
     */
    private void addLabelsToList(List<String> labels, List<String> list) {

        LOGGER.entry(labels,list);

        for (String label: labels) {

            String[] normalizedLabels = WordNormalizer.split(label);

            for (String normLabel : normalizedLabels){
                list.add(normLabel);
            }
        }

        LOGGER.exit();
    }

}
