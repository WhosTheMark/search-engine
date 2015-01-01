package model.sparql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to store the results of an SPARQL query. When created the current row
 * will be positioned before the first row, so this.nextRow() should be called.
 *
 */
class SparqlResult {

    private List<Map<String,String>> results;
    private int currentRow = -1;

    /**
     * Creates a new SparqResult object.
     */
    SparqlResult(){
        results = new ArrayList<Map<String, String>>();
    }

    /**
     * Adds a row to the results.
     */
    void addRow(){
        Map<String,String> row = new HashMap<String,String>();
        results.add(row);
    }

    /**
     * Adds a result to the last row.
     * @param key the name of the variable.
     * @param value the value associated to the variable in the query.
     * @return true if the value could be added.
     */
    boolean addResult(String key, String value){

        int size = results.size();

        if (size == 0){
            return false;
        }

        Map<String, String> map = results.get(size - 1);
        map.put(key,value);
        return true;
    }

    /**
     * Checks if there are more rows.
     * @return true if there are more rows.
     */
    boolean hasNextRow(){
        return currentRow + 1 < results.size();
    }

    /**
     * Moves the pointer to the next row in the result.
     */
    void nextRow(){
        ++currentRow;
    }

    /**
     * Gets the value of an associated key from the current row.
     * @param key the key to search.
     * @return the value associated or null if it does not exist.
     */
    String getValue(String key){

        if (currentRow < 0 || currentRow >= results.size()){
            return null;
        }

        return results.get(currentRow).get(key);
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for(Map<String,String> map : results){

            sb.append("[");
            for(Map.Entry<String, String>entry : map.entrySet()){
                sb.append("(" + entry.getKey() + "," + entry.getValue() + "),");
            }
            sb.append("]\n");
        }

        return sb.toString();
    }
}
