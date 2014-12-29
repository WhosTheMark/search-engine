package model;

import java.io.File;

import model.indexation.Indexer;

import org.junit.Test;

public class IndexerTest {

    @Test
    public void test() {

        long start = System.currentTimeMillis();

        File folder = new File("extra/CORPUS");
        File stopWordsfile = new File("extra/stopliste.txt");

        Indexer.startIndexation(stopWordsfile, folder);

        long end = System.currentTimeMillis();

        System.out.println(end - start);
    }

}
