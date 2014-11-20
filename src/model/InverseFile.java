package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.database.DBDriver;

public class InverseFile implements Iterable<Entry<String, Integer>> {

    private Map<String, Integer> map;
    private int documentId;
    private String documentName;
    private int numberOfWords = 0;
    private static final Logger LOGGER = Logger.getLogger(InverseFile.class.getName());

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

        LOGGER.log(Level.FINEST, "Calculating frequency for word " + word);

        ++numberOfWords;
        int frequency = 1;

        if (map.containsKey(word)) {
            frequency = 1 + map.get(word);
        }

        map.put(word, frequency);

    }
    
    /*
     * Stores the inverse file in the database.
     */
    public void storeInDB(){

        boolean stored = DBDriver.storeDocument(this.documentId, this.documentName);

        if (stored) {
            for (Entry<String, Integer> elem : this) {
                DBDriver.storeWord(elem.getKey());
                DBDriver.storeInverseTfEntry(elem.getKey(), documentId, elem.getValue());
            }
        }
    }

    @Override
    public Iterator<Entry<String, Integer>> iterator() {
        return map.entrySet().iterator();
    }

}
