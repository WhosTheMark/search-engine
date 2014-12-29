package model.database.layer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import model.search.RelevantDocument;

public class DBLayer {

    private static final Logger LOGGER = LogManager.getLogger();

    //Names of tables
    private static final String WORD_TABLE = "word";
    private static final String INDEX_TABLE = "indx";
    private static final String INDEX_TF_IDF_TABLE = "tf_idf_index";
    private static final String DOC_TABLE = "document";

    //Queries
    private static final String INSERT_DOC = "INSERT INTO " + DOC_TABLE + " VALUES (?,?);";
    private static final String SELECT_NUMBER_OF_DOCS = "SELECT COUNT(*) " +
                                                        "FROM " + DOC_TABLE + ";";
    private static final String SELECT_WORD = "SELECT * " +
                                              "FROM " + WORD_TABLE +
                                              " WHERE id_word=?;";
    private static final String INSERT_WORD = "INSERT INTO " + WORD_TABLE + " VALUES (?);";

    private static final String DELETE_STMT = "DELETE FROM ?";

    private Connection connection;

    public DBLayer() {
        connection = ConnectionBuilder.getConnection();
    }

    /*
     * Store a document in the database.
     */
    public boolean storeDocument(int idDocument, String documentName) {

        LOGGER.debug("Storing document {}.", documentName);

        try {

            PreparedStatement prepstmt = connection.prepareStatement(INSERT_DOC);
            prepstmt.setInt(1, idDocument);
            prepstmt.setString(2, documentName);
            prepstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("Could not add a document to database.", e);
            return false;
        }

        return true;
    }

    public int getNumberOfDocuments(){

        LOGGER.trace("Getting the number of docs");

        ResultSet rs;
        try {
            PreparedStatement prepstmt = connection.prepareStatement(SELECT_NUMBER_OF_DOCS);
            rs = prepstmt.executeQuery();

            if(!rs.next()){
                return 0;
            }

            return rs.getInt(1);

        } catch (SQLException e) {
            LOGGER.fatal("There is not a table " + DOC_TABLE + " in the database.", e);
            return -1;
        }
    }
    /*
     * Checks if a wordExists in the database.
     */
    private boolean wordExists(String word) throws SQLException {

        PreparedStatement prepstmt = connection.prepareStatement(SELECT_WORD);
        prepstmt.setString(1, word);
        ResultSet rs = prepstmt.executeQuery();

        return rs.next();
    }

    /*
     * Store a word in the database.
     */
    public void storeWord(String word) {

        try {

            if (!wordExists(word)) {

                PreparedStatement prepstmt = connection
                        .prepareStatement(INSERT_WORD);
                prepstmt.setString(1, word);
                prepstmt.executeUpdate();

            }

        } catch (SQLException e) {
            LOGGER.warn("Could not store word \"{}\" in the database, "
                    + "other thread might have stored it.", word);
        }
    }

    /*
     * Store an entry of the inverse file in the database.
     */
    public void storeInverseTfEntry(String word, int document, float weight) {

        storeInverseEntry(word, document, weight, INDEX_TABLE);
    }

    public void storeInverseTfIdfEntry(String word, int document, float weight) {
        storeInverseEntry(word, document, weight, INDEX_TF_IDF_TABLE);
    }

    private void storeInverseEntry(String word, int document,
            float weight, String table) {

        String strStmt = "INSERT INTO " + table + " VALUES (?,?,?);";

        try {

            PreparedStatement prepstmt = connection.prepareStatement(strStmt);
            prepstmt.setString(1, word);
            prepstmt.setInt(2, document);
            prepstmt.setFloat(3, weight);
            prepstmt.execute();

        } catch (SQLException e) {
            LOGGER.error("Could not store index in the database.",e);
        }
    }

    /*
     * Returns the list of relevant document of a word using Term Frequency.
     */
    public List<RelevantDocument> getRelevantDocsTf(String word) {

        LOGGER.trace("Getting relevant documents of the word {} using tf.", word);

        return getRelevantDocs(word,INDEX_TABLE);
    }

    /*
     * Returns the list of relevant document of a word using tf-idf.
     */
    public List<RelevantDocument> getRelevantDocsTfIdf(String word) {

        LOGGER.trace("Getting relevant documents of the word {} using tf-idf.", word);

        return getRelevantDocs(word,INDEX_TF_IDF_TABLE);
    }

    private List<RelevantDocument> getRelevantDocs(String word, String table) {

        String strQuery = "SELECT * FROM " + table + " , " + DOC_TABLE
                + " WHERE id_document = document AND id_word=? "
                + "order by id_document;";
        PreparedStatement prepstmt;
        List<RelevantDocument> list;

        try {
            prepstmt = connection.prepareStatement(strQuery);
            prepstmt.setString(1, word);
            ResultSet rs = prepstmt.executeQuery();
            list = buildRelevantDocList(rs);

        } catch (SQLException e) {
            LOGGER.error("A problem occurred getting the relevant documents of a word.",
                    e);
            list = new ArrayList<RelevantDocument>();
        }

        return list;
    }

    /*
     * Builds relevant document list using a result set.
     */
    private List<RelevantDocument> buildRelevantDocList(ResultSet rs)
            throws SQLException{

        List<RelevantDocument> list = new ArrayList<RelevantDocument>();

        while (rs.next()) {
            int documentId = rs.getInt("id_document");
            String docName = rs.getString("name");
            int weight = rs.getInt("weight");

            RelevantDocument aux = new RelevantDocument(documentId, docName,
                    weight);

            list.add(aux);
        }

        return list;
    }

    /*
     * Deletes the information of the tables in the database.
     */
    public void eraseDB() {

        LOGGER.debug("Deleting info from databse.");

        try {
            PreparedStatement prepstmt = connection.prepareStatement(DELETE_STMT);
            prepstmt.setString(1, INDEX_TABLE);
            prepstmt.executeUpdate();
            prepstmt.setString(1, WORD_TABLE);
            prepstmt.executeUpdate();
            prepstmt.setString(1, DOC_TABLE);
            prepstmt.executeUpdate();
            prepstmt.setString(1, INDEX_TF_IDF_TABLE);
            prepstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.error("A problem occurred ereasing the tables of the database.",
                    e);
        }

    }

    public void finalize(){

        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("A problem occurred closing the connection of the database.",
                    e);
        }
    }

}
