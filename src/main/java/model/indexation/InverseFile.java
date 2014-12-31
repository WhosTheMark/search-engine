package model.indexation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class to store the words found in a document.
 * It stores the name of the document an associated ID, all the words found and
 * how many times the word has been found.
 */
public class InverseFile implements Iterable<Entry<String, Integer>> {

    private Map<String, Integer> map;
    private final int documentId;
    private final String documentName;
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Creates an Inverse File with the information of the document.
     * @param documentId ID of the associated document.
     * @param documentName name of the associated document.
     */
    InverseFile(int documentId, String documentName){
        this.documentId = documentId;
        this.documentName = documentName;
        this.map = new HashMap<String, Integer>();
    }

    /**
     * Gets the ID of the associated document.
     * @return an ID.
     */
    public int getDocumentId() {
        return documentId;
    }

    /**
     * Gets the name of the associated document.
     * @return the name of the document.
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * Add a word in the inverse file. If the word is not already in the inverse file
     * then its frequency is 1, else it is the frequency it had + 1.
     */
    void addWord(String word){

        LOGGER.entry(word);

        int frequency = 1;

        if (map.containsKey(word)) {
            frequency = 1 + map.get(word);
        }

        map.put(word, frequency);
        LOGGER.trace("Added word: {} with frequency: {}", word, frequency);

    }

    @Override
    public Iterator<Entry<String, Integer>> iterator() {
        return map.entrySet().iterator();
    }

}
