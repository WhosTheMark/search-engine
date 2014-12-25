package model;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.database.DBAccessor;

/*
 * Stores the data of a relevant document.
 */
public class RelevantDocument implements Comparable<RelevantDocument> {

    private static final Logger LOGGER = LogManager.getLogger();
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
        float subs = o.weight - this.weight;

        if (subs > 0) {
            return 1;
        } else if (subs < 0) {
            return -1;
        }

        return 0;
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

        LOGGER.entry(keyword);

        List<RelevantDocument> listTfIdf = DBAccessor.getRelevantDocsTfIdf(keyword);

        // If there is no data of tf-idf in the database, we calculate it.
        if (listTfIdf.isEmpty()) {
            List<RelevantDocument> listTf = DBAccessor.getRelevantDocsTf(keyword);
            int numDocs = DBAccessor.getNumberOfDocuments();

            if (numDocs > 0) {
                listTfIdf = calculateTfIdf(keyword,listTf,numDocs);
            }
        }

        return LOGGER.exit(listTfIdf);
    }


    private static List<RelevantDocument> calculateTfIdf (String keyword,
            List<RelevantDocument> list, int numDocs){

        LOGGER.entry(keyword,list,numDocs);

        double idf = Math.log((float) (numDocs) / (float)(1 + list.size()));

        for(RelevantDocument doc : list){
            doc.weight *= idf;
        }

        storeTfIdf(keyword, list);
        return LOGGER.exit(list);
    }

    /*
     * Store the results for memoization
     */
    private static void storeTfIdf(String keyword, List<RelevantDocument> list) {

        for(RelevantDocument doc : list){
            DBAccessor.storeInverseTfIdfEntry(keyword,doc.documentId,doc.weight);
        }
    }

}
