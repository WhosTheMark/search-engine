package model.sparql;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to implement methods to manipulate the information gotten from the server.
 */
public class SparqlDAO {

    private static final Logger LOGGER = LogManager.getLogger();
    // Connection to the server.
    private SparqlClient client;
    private static final String PREFIXES =
              "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "prefix : <http://www.irit.fr/recherches/MELODI/ontologies/FilmographieV1.owl#>";

    private static final String SERVER_ADDRESS = "localhost:3030/ds";

    /**
     * Creates a new data access object and connects to the server.
     */
    public SparqlDAO() {
        this.client = new SparqlClient(SERVER_ADDRESS);
    }

    /**
     * Gets the labels or synonymous of a string.
     * @param label the string used to search the synonymous.
     * @return the list of synonymous.
     */
    public List<String> getOtherLabels(String label){

        LOGGER.entry(label);

        List<String> list = new ArrayList<String>();

        String query = PREFIXES + "SELECT ?label WHERE { ?res rdfs:label \"" + label + "\" @fr."
                                + "?res rdfs:label ?label."
                                + "FILTER (?label != \"" + label + "\" @fr)."
                                + "FILTER langMatches(lang(?label), \"FR\" ). }";
        SparqlResult results = client.select(query);

        while (results.hasNextRow()){
            results.nextRow();
            String value = results.getValue("label");
            list.add(value);
        }

        return LOGGER.exit(list);
    }

    public List<String> getSubClassLabels(String label) {

        String uri = getURI(label);

        if(uri == null){
            return new ArrayList<String>();
        }

        return calculateSubclassClousure(uri);
    }

    private List<String> calculateSubclassClousure(String uri) {

        List<String> labels = new ArrayList<String>();
        Set<String> uris = new HashSet<>();
        uris.add(uri);

        while(!uris.isEmpty()){
            uris = getDirectSubclasses(uris, labels);
        }

        return labels;
    }

    private Set<String> getDirectSubclasses(Set<String> uris, List<String> labels) {

        Set<String> newURIs = new HashSet<>();

        for(String uri : uris){
            String query = PREFIXES + "SELECT * WHERE { ?subClass rdfs:subClassOf <" + uri + ">."
                                    + "?subClass rdfs:label ?label."
                                    + "FILTER langMatches(lang(?label), \"FR\" ).}";
            SparqlResult results = client.select(query);

            getSubclassesData(results, labels, newURIs);
        }

        return newURIs;
    }

    private void getSubclassesData(SparqlResult results, List<String> labels,
            Set<String> newURIs) {

        while (results.hasNextRow()) {

            results.nextRow();
            String subClassURI = results.getValue("subClass");

            if (!newURIs.contains(subClassURI)) {
                newURIs.add(subClassURI);
            }

            String subclassLabel = results.getValue("label");
            labels.add(subclassLabel);
        }
    }

    private String getURI(String label){

        String query = PREFIXES + "SELECT ?res WHERE { ?res rdfs:label \"" + label +"\" @fr.}";
        SparqlResult results = client.select(query);
        results.nextRow();
        return results.getValue("res");
    }

}