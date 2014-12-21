package model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.sparql.SparqlAccessor;

public class QueryEnhancer {

    private static final Logger LOGGER = Logger.getLogger(QueryEnhancer.class.getName());
    private SparqlAccessor sparqlDAO;
    private static final String SEPARATOR_REGEXP = ",";

    public QueryEnhancer(){
        sparqlDAO =  new SparqlAccessor();
    }

    public List<String> enhanceQuery(String query){

        LOGGER.log(Level.INFO, "Procesing original query: " + query);
        String[] terms = query.split(SEPARATOR_REGEXP);
        return enhanceQuery(terms);
    }

    public List<String> enhanceQuery(String[] terms){

        List<String> list = new ArrayList<String>();

        for (String term : terms){
            String trimmedTerm = term.trim();
            List<String> labels = sparqlDAO.getOtherLabels(trimmedTerm);

            addLabelsToList(labels, list);
        }

        LOGGER.log(Level.INFO, "Found " + list.size() + " word(s) to enhance query: "
                + list);

        return list;
    }

    private void addLabelsToList(List<String> labels, List<String> list) {

        for (String label: labels) {

            String[] normalizedLabels = label.split(Indexation.SEPARATOR_REGEXP);

            for (String normLabel : normalizedLabels){
                list.add(normLabel);
            }
        }
    }

}
