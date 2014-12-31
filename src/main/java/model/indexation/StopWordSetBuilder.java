package model.indexation;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to build the set of words to be ignored. It will use a File, read each
 * line and normalized the words so they match with the normalized words of the
 * HTML files.
 */
class StopWordSetBuilder {

    private static final Logger LOGGER = LogManager.getLogger();

    // Avoid instantiation.
    private StopWordSetBuilder(){}

    /**
     * Creates the set of words to not be included in the database.
     * @param file where the words will be found.
     */
    static Set<String> createStopWordsSet(File file) {

        LOGGER.debug("Building stop words set using file {}.", file.getName());
        Scanner scanner;
        Set<String> set = new HashSet<String>();

        try {
            scanner = new Scanner(file);

        } catch (FileNotFoundException e) {
            LOGGER.error("Could not find stop list file.", e);
            return set;
        }

        WordNormalizer.setDelimiter(scanner);
        addWordsToSet(scanner, set);

        LOGGER.exit(set);
        return set;
    }

    /**
     * Adds words to the set using a file scanner. It normalizes the word and
     * then it adds it.
     * @param scanner the text scanner of the file.
     * @param set the set of words to be ignored.
     * @see WordNormalizer
     */
    static void addWordsToSet(Scanner scanner, Set<String> set) {

        LOGGER.entry(scanner,set);

        while (scanner.hasNext()) {
            String word = scanner.next();
            String normalizedWord = WordNormalizer.normalize(word);
            set.add(normalizedWord);
        }

        LOGGER.exit();
    }
}
