package model.evaluation;

import java.util.Set;

/**
 * Class to store the information of an evaluation.
 */
public class Evaluation {

    private static final int MAX_PRECISION = 25;

    // Set that contains the list of relevant documents in the qrel file.
    private Set<String> relevantDocs;

    float precision5 = 0;
    float precision10 = 0;
    float precision25 = 0;
    private int totalDocsFound = 0;
    private int relevantDocsFound = 0;

    // For the final evaluation, TODO  new class with this.
    float averageRappel = 0;

    /**
     * Creates a new Evaluation.
     * Assigns MAX_PRECISION to avoid any update of the fields by using check 
     * documents.
     */
    Evaluation() {
        totalDocsFound = MAX_PRECISION;
    }

    /**
     * Creates a new Evaluation using the set of relevant documents.
     * @param relevantDocs the set of relevant documents.
     */
    Evaluation(Set<String> relevantDocs){
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

    public float getRappel() {

        if (relevantDocs != null) {
            return (float) totalDocsFound / (float) relevantDocs.size();
        } else {
            return averageRappel;
        }
    }

    /**
     * Checks if the document is relevant or not and updates precisions.
     * @param document to check.
     */
    void checkDocument(String document){

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
                + "\nP25: " + precision25 + "\nRappel: " + getRappel();
    }
}
