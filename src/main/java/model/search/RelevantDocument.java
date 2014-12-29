package model.search;

/*
 * Stores the data of a relevant document.
 */
public class RelevantDocument implements Comparable<RelevantDocument> {

    private final int documentId;
    private final String documentName;
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
}
