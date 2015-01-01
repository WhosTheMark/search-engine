package model.search;

/**
 * Stores the data of a relevant document. It stores the id of the document
 * in the database, its name and a float that indicates the relevance of this
 * document to the query. The higher the relevance, the better.
 */
public class RelevantDocument implements Comparable<RelevantDocument> {

    private final int documentId;
    private final String documentName;
    private float relevance = 0;

    /**
     * Creates a relevant document.
     * @param documentId the id of the document.
     * @param documentName the name of the document.
     * @param relevance the initial relevance of the document.
     */
    public RelevantDocument(int documentId, String documentName, float relevance) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.relevance = relevance;
    }

    /**
     * Gets the ID of the document in the database.
     * @return the ID of the document.
     */
    public int getDocumentId() {
        return documentId;
    }

    /**
     * Gets the name of the document.
     * @return the name of the document.
     */
    public String getDocumentName() {
        return documentName;
    }

    /**
     * Gets the relevance of the document.
     * @return the relevance of the document.
     */
    public float getRelevance() {
        return relevance;
    }

    /**
     * Sets the relevance of the document.
     * @param relevance the new relevance.
     */
    public void setRelevance(float relevance) {
        this.relevance = relevance;
    }

    @Override
    public int compareTo(RelevantDocument o) {
        float subs = o.relevance - this.relevance;

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
        return (int) (prime + relevance);
    }

    @Override
    public boolean equals(Object obj) {

        if ((obj == null) || (obj.getClass() != this.getClass())){
            return false;
        }

        RelevantDocument doc = (RelevantDocument) obj;
        return this.relevance == doc.relevance;
    }
}
