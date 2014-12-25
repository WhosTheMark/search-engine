package model.sparql;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SparqlAccessor {

    private static final Logger LOGGER = LogManager.getLogger();
    // Connection to the server.
    private SparqlClient client;
    private static final String PREFIXES =
              "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "prefix : <http://www.irit.fr/recherches/MELODI/ontologies/FilmographieV1.owl#>";

    private static final String SERVER_ADDRESS = "localhost:3030/ds";

    public SparqlAccessor() {
        this.client = new SparqlClient(SERVER_ADDRESS);
    }

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

}