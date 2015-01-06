package model.search.calculators;

import java.util.List;

import model.search.RelevantDocument;

/**
 * Interface to implement Strategy Pattern. This lets you change dynamically
 * the behavior of searchers. A weight might be used to multiply the relevance
 * of the documents found.
 */
public abstract class RelevanceCalculator {

    //The default weight for a word.
    protected static final int DEFAULT_WEIGHT = 1;

    /**
     * Adds the documents to the calculator using the default weight.
     * @param relevDocs the list of docs to add.
     */
    public abstract void addDocuments(List<RelevantDocument> relevDocs);

    /**
     * Adds the documents to the calculator using a given weight weight.
     * @param weight
     * @param relevDocs
     */
    public abstract void addDocuments(int weight, List<RelevantDocument> relevDocs);

    /**
     * Finalizes the calculations and returns the list with the results.
     * @return the list with the relevant documents and their relevance.
     */
    public abstract List<RelevantDocument> calculateRelevantDocs();
}
