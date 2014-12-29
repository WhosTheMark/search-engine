package model.search.calculators;

import java.util.List;

import model.search.RelevantDocument;

/*
 * Interface to implement Strategy Pattern.
 */
public abstract class RelevanceCalculator {

    protected static final int DEFAULT_WEIGHT = 1;

    public abstract void addDocuments(List<RelevantDocument> relevDocs);
    public abstract void addDocuments(int weight, List<RelevantDocument> relevDocs);

    public abstract List<RelevantDocument> calculateRelevantDocs();
}
