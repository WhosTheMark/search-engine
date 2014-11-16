package model.calculators;

import java.util.List;

import model.RelevantDocument;

/*
 * Interface to implement Strategy Pattern.
 */
public interface RelevanceCalculator {

    public void calculateRelevance(List<RelevantDocument> relevDocs);

    public List<RelevantDocument> finalizeCalcs();
}
