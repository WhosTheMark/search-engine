package model.sparql;

import java.util.ArrayList;
import java.util.List;

public class SparqlDriver {
    /**
     * URI of the remote SPARQL server
     */
    private SparqlClient client;
    private static final String PREFIXES =
              "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "prefix : <http://www.irit.fr/recherches/MELODI/ontologies/FilmographieV1.owl#>";

    public SparqlDriver(String endpointUri) {
        this.client = new SparqlClient(endpointUri);
    }

    public List<String> getURIs(String label) {

        List<String> list = new ArrayList<String>();

        String query = PREFIXES + " SELECT * WHERE { ?res rdfs:label \"" + label + "\" @fr. }";
        SparqlResult results = client.select(query);

        while (results.hasNextRow()){
            results.nextRow();
            String value = results.getValue("res");
            list.add(value);
        }

        return list;
    }

    public List<String> getAllLabels(String uri){

        List<String> list = new ArrayList<String>();

        String query = PREFIXES + " SELECT * WHERE { <" + uri + "> rdfs:label ?label }";
        SparqlResult results = client.select(query);

        while (results.hasNextRow()){
            results.nextRow();
            String value = results.getValue("label");
            list.add(value);
        }

        return list;
    }
}