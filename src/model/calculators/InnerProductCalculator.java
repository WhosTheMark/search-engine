package model.calculators;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.RelevantDocument;

/*
 * Calculates relevant documents by using inner product.
 */
public class InnerProductCalculator implements RelevanceCalculator {

    private static final Logger LOGGER = Logger.getLogger(
            InnerProductCalculator.class.getName());

    /*
     * Calculates the relevance of documents using a list that accumulates the
     * result and a list of the newly obtained frequencies.
     */
    @Override
    public List<RelevantDocument> calculateRelevance(List<RelevantDocument> accum,
            List<RelevantDocument> freqs) {

        LOGGER.log(Level.FINER, "Calculating frquencies using inner product.");

        List<RelevantDocument> result = new ArrayList<RelevantDocument>();
        int i = 0;
        int j = 0;

        //Merge the two lists ordered by the id of the document
        while (i < accum.size() && j < freqs.size()) {

            RelevantDocument elem1 = accum.get(i);
            RelevantDocument elem2 = freqs.get(j);

            if (elem1.getDocumentId() < elem2.getDocumentId()) {
                result.add(elem1);
                ++i;

            } else if (elem1.getDocumentId() > elem2.getDocumentId()) {
                result.add(elem2);
                ++j;

            } else {
                elem1.setWeight(elem1.getWeight() + elem2.getWeight());
                result.add(elem1);
                ++i;
                ++j;
            }
        }

        addLastElems(i,accum,result);
        addLastElems(j,freqs,result);

        return result;

    }

    /*
     * Add the elements of the list that have not been added.
     */
    private void addLastElems(int i, List<RelevantDocument> list, 
            List<RelevantDocument> result){

        LOGGER.log(Level.FINEST, 
                "Adding the last elements on the list starting at index: "
                + i);

        for (; i < list.size(); ++i) {
            RelevantDocument elem = list.get(i);
            result.add(elem);
        }
    }

}
