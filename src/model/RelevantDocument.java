package model;

import java.util.List;

import model.database.DBDriver;

/*
 * Stores the data of a relevant document.
 */
public class RelevantDocument implements Comparable<RelevantDocument> {

    private int documentId;
    private String documentName;
    private float weight = 0;

    public RelevantDocument(int documentId, String documentName, float weight) {
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

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public int compareTo(RelevantDocument o) {
        return (int) (o.weight - this.weight);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        return (int) (prime + weight);
    }

    @Override
    public boolean equals(Object obj) {

        if ((obj == null) || (obj.getClass() != this.getClass())){
            return false;
        }

        RelevantDocument doc = (RelevantDocument) obj;
        return this.weight == doc.weight;
    }

    /*
     * Get tf-idf relevant documents of a keyword from the database
     */
    public static List<RelevantDocument> getRelevantDocs(String keyword){

        List<RelevantDocument> listTfIdf = DBDriver.getRelevantDocsTfIdf(keyword);

        // If there is no data of tf-idf in the database, we calculate it.
        if (listTfIdf.isEmpty()) {
            List<RelevantDocument> listTf = DBDriver.getRelevantDocsTf(keyword);
            int numDocs = DBDriver.getNumberOfDocuments();
            listTfIdf = calculateTfIdf(keyword,listTf,numDocs);
        }

        return listTfIdf;
    }


    private static List<RelevantDocument> calculateTfIdf (String keyword,
            List<RelevantDocument> list, int numDocs){

        double idf = Math.log((float) (numDocs) / (float)(1 + list.size()));

        for(RelevantDocument doc : list){
            doc.weight *= idf;
        }

        storeTfIdf(keyword, list);
        return list;
    }

    /*
     * Store the results for memoization
     */
    private static void storeTfIdf(String keyword, List<RelevantDocument> list) {

        for(RelevantDocument doc : list){
            DBDriver.storeInverseTfIdfEntry(keyword,doc.documentId,doc.weight);
        }
    }

}
