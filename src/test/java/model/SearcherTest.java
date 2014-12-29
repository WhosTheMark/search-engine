package model;

import java.io.File;

import model.search.Searcher;
import model.search.calculators.InnerProductCalculator;

import org.junit.Test;

public class SearcherTest {


    final String QUERY_FILE = "extra/queries.txt";
    final String RESULT_FOLDER = "extra/results";

    @Test
    public void testFile(){

        File file = new File(QUERY_FILE);
        Searcher searcher = new Searcher(new InnerProductCalculator(), RESULT_FOLDER);
        searcher.executeQueriesFromFile(file);
    }

}
