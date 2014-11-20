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

    private List<RelevantDocument> accumDocs;

    public InnerProductCalculator(){
        accumDocs = new ArrayList<RelevantDocument>();
    }

    /*
     * Calculates the relevance of documents with list of the newly
     * obtained frequencies.
     */
    @Override
    public void calculateRelevance(List<RelevantDocument> relevDocs) {

        LOGGER.log(Level.FINER, "Calculating relevance using inner product.");

        List<RelevantDocument> result = new ArrayList<RelevantDocument>();
        int i = 0;
        int j = 0;

        // Merge the two lists ordered by the id of the document
        while (i < accumDocs.size() && j < relevDocs.size()) {

            RelevantDocument elem1 = accumDocs.get(i);
            RelevantDocument elem2 = relevDocs.get(j);

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

        addLastElems(i,accumDocs,result);
        addLastElems(j,relevDocs,result);

        accumDocs = result;

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

    /*
     * Finalize the calculations.
     */
    @Override
    public List<RelevantDocument> finalizeCalcs() {
        List<RelevantDocument> aux = accumDocs;
        accumDocs = new ArrayList<RelevantDocument>();
        return aux;
    }

}
