package model;

import java.util.List;

import model.database.DBDriver;

/*
 * Stores the data of a relevant document.
 */
public class RelevantDocument implements Comparable<RelevantDocument> {

    private int documentId;
    private String documentName;
    private int weight = 0;

    public RelevantDocument(int documentId, String documentName, int weight) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.weight = weight;
    }

    public int getDocumentId() {
        return documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(RelevantDocument o) {
        return o.weight - this.weight;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return prime + weight;
    }

    @Override
    public boolean equals(Object obj) {

        if ((obj == null) || (obj.getClass() != this.getClass())){
            return false;
        }

        RelevantDocument doc = (RelevantDocument) obj;

        return this.weight == doc.weight;

    }

    public static List<RelevantDocument> getRelevantDocsFromDB(String keyword){
        return DBDriver.getRelevantDocs(keyword);
    }

}
