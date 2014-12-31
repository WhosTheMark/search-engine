package model.indexation;

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to normalize words and split strings into words. It uses a regular
 * expression containing the letters of the French alphabet to separate words.
 * For normalizing it will truncate any word longer than seven characters and
 * will lower case the letters of the words.
 */
public class WordNormalizer {

    private static final Logger LOGGER = LogManager.getLogger();

    /** The regular expression used to separate the words in a file.*/
    private static final String SEPARATOR_REGEXP = "[^A-Za-z0-9éàèùâêîôûëïüÿçœæ]+";

    // Maximum length of a word, if it is longer it will be truncated.
    private static final int WORD_MAX_LENGTH = 7;

    //Avoid instantiation.
    private WordNormalizer(){}

    /**
     * Normalizes a word by lowercasing it and truncating it if it is too long.
     * @param keyword word to normalize.
     * @return the normalized keyword.
     */
    public static String normalize(String keyword) {

        LOGGER.entry(keyword);

        String lowerCaseWord = keyword.toLowerCase();

        // Truncate string if it is too long
        if (lowerCaseWord.length() > WORD_MAX_LENGTH){
            lowerCaseWord = lowerCaseWord.substring(0,WORD_MAX_LENGTH);
        }

        return LOGGER.exit(lowerCaseWord);
    }

    /**
     * Splits a query in different words.
     * @param query query to split.
     * @return an array of words.
     */
    public static String[] split(String query){
        return query.split(SEPARATOR_REGEXP);
    }

    /**
     * Sets the delimiter of a text scanner. It is used to separate the words
     * of a file into words.
     * @param scanner the text scanner to modify.
     */
    public static void setDelimiter(Scanner scanner){
        scanner.useDelimiter(SEPARATOR_REGEXP);
    }

}
