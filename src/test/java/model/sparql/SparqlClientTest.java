package model.sparql;

public class SparqlClientTest {

    private static final String PREFIXES =
              "PREFIX : <http://www.lamaisondumeurtre.fr#>\n"
            + "PREFIX instances: <http://www.lamaisondumeurtre.fr/instances#>\n";

    private static final String INSERT_PERSON =
              PREFIXES
            + "INSERT DATA\n"
            + "{\n"
            + "  instances:Bob :personneDansPiece instances:Bureau.\n"
            + "}\n";

    private static final String DELETE_PERSON =
              PREFIXES
            + "DELETE DATA\n"
            + "{\n"
            + "  instances:Bob :personneDansPiece instances:Bureau.\n"
            + "}\n";

    private static final String NUMBER_PEOPLE =
              PREFIXES
            + "SELECT ?piece (COUNT(?personne) AS ?nbPers) WHERE\n"
            + "{\n"
            + "    ?personne :personneDansPiece ?piece.\n"
            + "}\n"
            + "GROUP BY ?piece\n";

    private static final String SERVER_ADDRESS = "localhost:3030/ds";

   // @Test
    public void test() {
        SparqlClient sparqlClient = new SparqlClient(SERVER_ADDRESS);


        if (sparqlClient.isServerUp()) {

            System.out.println("server is UP");

            nbPersonnesParPiece(sparqlClient);

            System.out.println("ajout d'une personne dans le bureau:");
            sparqlClient.update(INSERT_PERSON);

            nbPersonnesParPiece(sparqlClient);

            System.out.println("suppression d'une personne du bureau:");
            sparqlClient.update(DELETE_PERSON);

            nbPersonnesParPiece(sparqlClient);

        } else {
            System.out.println("service is DOWN");
        }
    }

    private static void nbPersonnesParPiece(SparqlClient sparqlClient) {

            SparqlResult results = sparqlClient.select(NUMBER_PEOPLE);
            System.out.println("nombre de personnes par pièce:");

            while (results.hasNextRow()) {
                results.nextRow();
                System.out.println(results.getValue("piece") + " : " + results.getValue("nbPers"));
            }
    }


}
