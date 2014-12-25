package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.sparql.SparqlAccessor;

public class QueryEnhancer {

    private static final Logger LOGGER = LogManager.getLogger();
    private SparqlAccessor sparqlDAO;
    private static final String SEPARATOR_REGEXP = ",";

    public QueryEnhancer(){
        sparqlDAO =  new SparqlAccessor();
    }

    public List<String> enhanceQuery(String query){

        LOGGER.info("Procesing original query: {}.", query);

        String[] terms = query.split(SEPARATOR_REGEXP);
        return enhanceQuery(terms);
    }

    public List<String> enhanceQuery(String[] terms){

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

    private void addLabelsToList(List<String> labels, List<String> list) {

        LOGGER.entry(labels,list);

        for (String label: labels) {

            String[] normalizedLabels = label.split(Indexation.SEPARATOR_REGEXP);

            for (String normLabel : normalizedLabels){
                list.add(normLabel);
            }
        }

        LOGGER.exit();
    }

}
