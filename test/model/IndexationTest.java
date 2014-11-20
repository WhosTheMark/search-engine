package model;

import java.io.File;

import model.Indexation;

import org.junit.Test;

public class IndexationTest {

    @Test
    public void test() {

        File folder = new File("extra/CORPUS");
        File stopWordsfile = new File("extra/stopliste.txt");

        Indexation.startIndexation(stopWordsfile, folder);
    }

}
