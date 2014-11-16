package controlleur;

import static org.junit.Assert.*;

import java.util.List;

import model.RelevantDocument;
import model.calculators.InnerProductCalculator;

import org.junit.Test;

import controlleur.Search;

public class SearchTest {

    @Test
    public void test() {

        String[] array = new String[2];
        array[0] = "personnes";
        array[1] = "Intouchables";
        Search search = new Search(new InnerProductCalculator());
        List<RelevantDocument> result = search.getRelevantDocs(array);

        for (RelevantDocument doc : result) {

            System.out.println("Doc: \t" + doc.getDocumentName() + "\t id:"
                    + doc.getDocumentId() + "\t Weight: " + doc.getWeight());

        }

        System.out.println("Finished.");
        fail("Not yet implemented");
    }

}
