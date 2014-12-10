package model;

import java.io.File;

import model.Searcher;
import model.calculators.InnerProductCalculator;

import org.junit.Test;

public class SearcherTest {


    @Test
    public void testFile(){

        File file = new File("extra/queries.txt");
        Searcher search = new Searcher(new InnerProductCalculator(), "extra/results");
        search.executeQueriesFromFile(file);

    }

}
