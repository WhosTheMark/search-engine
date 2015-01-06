package model;

import java.io.File;
import java.util.Scanner;

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

    //@Test
    public void testSingle(){

        Searcher searcher = new Searcher(new InnerProductCalculator(), RESULT_FOLDER);
        Scanner reader = new Scanner(System.in);
        System.out.print("Enter your query: ");
        String input = reader.nextLine();
        searcher.executeSingleQuery(input);
        reader.close();
        System.out.print("Finished!");
    }

}
