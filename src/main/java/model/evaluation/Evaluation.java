package model.evaluation;

import java.util.Set;

/**
 * Class to store the information of an evaluation.
 */
public class Evaluation {

    public static final int MAX_PRECISION = 25;

    // Set that contains the list of relevant documents in the qrel file.
    private Set<String> relevantDocs;

    private float precision5 = 0;
    private float precision10 = 0;
    private float precision25 = 0;
    private int totalDocsFound = 0;
    private int relevantDocsFound = 0;

    /**
     * Creates a new Evaluation.
     */
    public Evaluation() {
        totalDocsFound = MAX_PRECISION;
        relevantDocsFound = MAX_PRECISION;
    }

    /**
     * Creates a new Evaluation using the set of relevant documents.
     * @param relevantDocs the set of relevant documents.
     */
    public Evaluation(Set<String> relevantDocs){
        this.relevantDocs = relevantDocs;
    }

    /**
     * Gets the precision using the first five documents.
     * @return the value of the precision.
     */
    public float getPrecision5() {
        return precision5;
    }

    /**
     * Gets the precision using the first ten documents.
     * @return the value of the precision.
     */
    public float getPrecision10() {
        return precision10;
    }

    /**
     * Gets the precision using the first 25 documents.
     * @return the value of the precision.
     */
    public float getPrecision25() {
        return precision25;
    }

    public void setPrecision5(float precision5) {
        this.precision5 = precision5;
    }

    public void setPrecision10(float precision10) {
        this.precision10 = precision10;
    }

    public void setPrecision25(float precision25) {
        this.precision25 = precision25;
    }

    /**
     * Checks if the document is relevant or not and updates precisions.
     * @param document to check.
     */
    public void checkDocument(String document){

        ++totalDocsFound;

        if(relevantDocs.contains(document)){
            ++relevantDocsFound;
        }

        updatePrecision();
    }

    /**
     * Updates the values of the precisions if needed.
     */
    private void updatePrecision() {

        switch(totalDocsFound){

        case 5:
            precision5 =  (float)relevantDocsFound / (float)totalDocsFound;
            break;
        case 10:
            precision10 = (float)relevantDocsFound / (float)totalDocsFound;
            break;
        case 25:
            precision25 = (float)relevantDocsFound / (float)totalDocsFound;
            break;
        }
    }

    public String toString(){
        return "P5: " + precision5 + "\nP10: " + precision10
                + "\nP25: " + precision25;
    }
}
