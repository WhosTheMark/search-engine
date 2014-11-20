package model;

import java.io.File;

import model.Search;
import model.calculators.InnerProductCalculator;

import org.junit.Test;

public class SearchTest {


    @Test
    public void testFile(){

        File file = new File("extra/queries.txt");
        Search search = new Search(new InnerProductCalculator(), "extra/results");
        search.searchQueriesFromFile(file);

    }

}
