package model.database.DAO;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.database.layer.DBLayer;
import model.search.RelevantDocument;

/**
 * Class to retrieve the relevant documents from the database.
 * @see RelevantDocument
 */
public class RelevantDocumentDAO {

    private static final Logger LOGGER = LogManager.getLogger();
    private DBLayer sqlDAL;

    /**
     * Initializes the DAO and connects to the Data Access Layer.
     * @see DBLayer
     */
    public RelevantDocumentDAO() {
        sqlDAL = new DBLayer();
    }

    /**
     * Gets the list of relevant documents from the database using some keyword.
     * Each Relevant Document will be associated with a tf-idf weight. The first
     * time this method is called with a specific keyword it will calculate the
     * tf-idf weights and it will store them in the database. Any subsequent call
     * of this method using the same keyword will retrieve the data stored and
     * will not calculate the weights again in order to improve performance.
     * @param keyword searches in the database documents containing this String.
     * @return the list of relevant documents.
     */
    public List<RelevantDocument> getRelevantDocs(String keyword){

        LOGGER.entry(keyword);

        List<RelevantDocument> listTfIdf = sqlDAL.getRelevantDocsTfIdf(keyword);

        // If there is no data of tf-idf in the database, we calculate it.
        if (listTfIdf.isEmpty()) {
            List<RelevantDocument> listTf = sqlDAL.getRelevantDocsTf(keyword);
            int numDocs = sqlDAL.getNumberOfDocuments();

            if (numDocs > 0) {
                listTfIdf = calculateTfIdf(listTf,numDocs);
                storeTfIdf(keyword, listTfIdf);
            }
        }

        return LOGGER.exit(listTfIdf);
    }

    /**
     * Calculates tf-idf weights for the documents in list using the associated tf.
     * @param list has the documents with their tf weight associated.
     * @param numDocs number of documents in which the word appears.
     * @return list of documents with tf-idf weights.
     */
    private List<RelevantDocument> calculateTfIdf (List<RelevantDocument> list,
                    int numDocs){

        LOGGER.entry(list,numDocs);

        double idf = Math.log((float) (numDocs) / (float)(1 + list.size()));

        for(RelevantDocument doc : list){
            doc.setRelevance((float) (doc.getRelevance() * idf));
        }
        return LOGGER.exit(list);
    }

    /*
     * Stores the results of the calculated tf-idf weights in the database.
     */
    private void storeTfIdf(String keyword, List<RelevantDocument> list) {

        for(RelevantDocument doc : list){
            sqlDAL.storeInverseTfIdfEntry(keyword,doc.getDocumentId(),doc.getRelevance());
        }
    }

    /**
     * Closes the connection of the database.
     * It should be used after all the operations are done.
     */
    public void finalize() {
        sqlDAL.finalize();
    }
}
