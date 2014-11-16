import java.io.File;
import java.io.FileNotFoundException;

import controlleur.Indexation;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        File folder = new File("extra/CORPUS");
        File stopWordsfile = new File("extra/stopliste.txt");

        Indexation.indexFiles(stopWordsfile, folder);

    }

}
