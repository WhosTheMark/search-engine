package model.calculators;

import java.util.List;

import model.RelevantDocument;

/*
 * Interface to implement Strategy Pattern.
 */
public interface RelevanceCalculator {

    public List<RelevantDocument> calculateRelevance(List<RelevantDocument> docs,
            List<RelevantDocument> freqs);
}
