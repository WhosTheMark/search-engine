package model.database.DAO;

import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.database.layer.DBLayer;
import model.indexation.InverseFile;

/**
 * Class to store an InverseFile object in a database.
 * @see InverseFile
 */
public class InverseFileDAO {

    private static final Logger LOGGER = LogManager.getLogger();
    private DBLayer sqlDAL;

    /**
     * Initializes the DAO and connects to the Data Access Layer.
     * @see DBLayer
     */
    public InverseFileDAO() {
        sqlDAL = new DBLayer();
    }

    /**
     * Stores the entries of the InverseFile in the database.
     * @param inv The InverseFile object to store.
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

    /**
     * Closes the connection of the database.
     * It should be used after all the operations are done.
     */
    public void finalize() {
        sqlDAL.finalize();
        sqlDAL = null;
    }
}
