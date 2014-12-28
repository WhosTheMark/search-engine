package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InverseFile implements Iterable<Entry<String, Integer>> {

    private Map<String, Integer> map;
    private int documentId;
    private String documentName;
    private static final Logger LOGGER = LogManager.getLogger();

    public InverseFile(int documentId, String documentName){
        this.documentId = documentId;
        this.documentName = documentName;
        this.map = new HashMap<String, Integer>();
    }

    public int getDocumentId() {
        return documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    /*
     * Add a word in the inverse file. If the word is not already in the inverse file
     * then its frequency is 1, else is the frequency it had + 1
     */
    public void addWord(String word){

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
