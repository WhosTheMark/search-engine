package model;

import java.io.File;

import model.calculators.InnerProductCalculator;
import model.calculators.RelevanceCalculator;

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
