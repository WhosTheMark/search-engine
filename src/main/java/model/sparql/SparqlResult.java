package model.sparql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SparqlResult {

    private List<Map<String,String>> results;
    private int currentRow = -1;

    public SparqlResult(){
        results = new ArrayList<Map<String, String>>();
    }

    public void addRow(){
        Map<String,String> row = new HashMap<String,String>();
        results.add(row);
    }

    public boolean addResult(String key, String value){

        int size = results.size();

        if (size == 0){
            return false;
        }

        Map<String, String> map = results.get(size - 1);
        map.put(key,value);
        return true;
    }

    public boolean hasNextRow(){
        return currentRow + 1 < results.size();
    }

    public void nextRow(){
        ++currentRow;
    }

    public String getValue(String key){

        if (currentRow >= results.size()){
            return null;
        }

        return results.get(currentRow).get(key);
    }
}
