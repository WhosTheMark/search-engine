package model;

import java.io.File;

import model.search.EnhancedSearcher;
import model.search.calculators.InnerProductCalculator;
import model.search.calculators.RelevanceCalculator;

import org.junit.Test;

public class EnhancedSearcherTest {

    final String QUERY_FILE = "extra/queries.txt";
    final String RESULT_FOLDER = "extra/results";

    @Test
    public void test() {

        File file = new File(QUERY_FILE);
        RelevanceCalculator calc = new InnerProductCalculator();
        EnhancedSearcher searcher = new EnhancedSearcher(calc,RESULT_FOLDER);
        searcher.executeQueriesFromFile(file);
    }

}
