package model.search.calculators;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.search.RelevantDocument;

/*
 * Calculates relevant documents by using inner product.
 */
public class InnerProductCalculator extends RelevanceCalculator {

    private static final Logger LOGGER = LogManager.getLogger();

    private List<RelevantDocument> accumDocs;

    public InnerProductCalculator(){
        accumDocs = new ArrayList<RelevantDocument>();
    }

    @Override
    public void addDocuments(List<RelevantDocument> relevDocs) {
        addDocuments(RelevanceCalculator.DEFAULT_WEIGHT, relevDocs);
    }
    /*
     * Calculates the relevance of documents with list of the newly
     * obtained frequencies.
     */
    @Override
    public void addDocuments(int weight, List<RelevantDocument> relevDocs) {

        LOGGER.trace("Calculating relevance using inner product using weight {}.",
                weight);

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
                elem2.setWeight(weight * elem2.getWeight());
                result.add(elem2);
                ++j;

            } else {
                elem1.setWeight(elem1.getWeight() +  weight * elem2.getWeight());
                result.add(elem1);
                ++i;
                ++j;
            }
        }

        addLastElems(i,RelevanceCalculator.DEFAULT_WEIGHT,accumDocs,result);
        addLastElems(j,weight,relevDocs,result);

        accumDocs = result;

    }

    /*
     * Add the elements of the list that have not been added.
     */
    private void addLastElems(int index, int weight, List<RelevantDocument> list,
            List<RelevantDocument> result){

        for (; index < list.size(); ++index) {
            RelevantDocument elem = list.get(index);
            elem.setWeight(weight * elem.getWeight());
            result.add(elem);
        }
    }

    /*
     * Finalize the calculations.
     */
    @Override
    public List<RelevantDocument> calculateRelevantDocs() {
        List<RelevantDocument> aux = accumDocs;
        accumDocs = new ArrayList<RelevantDocument>();
        return aux;
    }

}
