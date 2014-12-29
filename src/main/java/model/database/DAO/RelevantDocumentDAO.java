package model.database.DAO;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.database.layer.DBLayer;
import model.search.RelevantDocument;

public class RelevantDocumentDAO {

    private static final Logger LOGGER = LogManager.getLogger();
    private DBLayer sqlDAL;

    public RelevantDocumentDAO() {
        sqlDAL = new DBLayer();
    }

    /*
     * Get tf-idf relevant documents of a keyword from the database
     */
    public List<RelevantDocument> getRelevantDocs(String keyword){

        LOGGER.entry(keyword);

        List<RelevantDocument> listTfIdf = sqlDAL.getRelevantDocsTfIdf(keyword);

        // If there is no data of tf-idf in the database, we calculate it.
        if (listTfIdf.isEmpty()) {
            List<RelevantDocument> listTf = sqlDAL.getRelevantDocsTf(keyword);
            int numDocs = sqlDAL.getNumberOfDocuments();

            if (numDocs > 0) {
                listTfIdf = calculateTfIdf(keyword,listTf,numDocs);
            }
        }

        return LOGGER.exit(listTfIdf);
    }


    private List<RelevantDocument> calculateTfIdf (String keyword,
            List<RelevantDocument> list, int numDocs){

        LOGGER.entry(keyword,list,numDocs);

        double idf = Math.log((float) (numDocs) / (float)(1 + list.size()));

        for(RelevantDocument doc : list){
            doc.setWeight((float) (doc.getWeight() * idf));
        }

        storeTfIdf(keyword, list);
        return LOGGER.exit(list);
    }

    /*
     * Store the results for memoization
     */
    private void storeTfIdf(String keyword, List<RelevantDocument> list) {

        for(RelevantDocument doc : list){
            sqlDAL.storeInverseTfIdfEntry(keyword,doc.getDocumentId(),doc.getWeight());
        }
    }

    public void finalize() {
        sqlDAL.finalize();
    }
}
