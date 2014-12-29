package model.database.DAO;

import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.database.layer.DBLayer;
import model.indexation.InverseFile;

public class InverseFileDAO {

    private static final Logger LOGGER = LogManager.getLogger();
    private DBLayer sqlDAL;

    public InverseFileDAO() {
        sqlDAL = new DBLayer();
    }

    /*
     * Stores the inverse file in the database.
     */
    public void store(InverseFile inv){

        LOGGER.entry();

        boolean stored = sqlDAL.storeDocument(inv.getDocumentId(), inv.getDocumentName());

        if (stored) {
            for (Entry<String, Integer> elem : inv) {
                sqlDAL.storeWord(elem.getKey());
                sqlDAL.storeInverseTfEntry(elem.getKey(), inv.getDocumentId(), elem.getValue());
            }
        }

        LOGGER.exit();
    }

    public void finalize() {
        sqlDAL.finalize();
    }
}
