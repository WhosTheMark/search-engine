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

    // Names of tables
    private static final String WORD_TABLE = "word";
    private static final String INDEX_TABLE = "indx";
    private static final String INDEX_TF_IDF_TABLE = "tf_idf_index";
    private static final String DOC_TABLE = "document";

    // Queries
    private static final String INSERT_DOC = "INSERT INTO " + DOC_TABLE + " VALUES (?,?);";
    private static final String SELECT_NUMBER_OF_DOCS = "SELECT COUNT(*) " +
                                                        "FROM " + DOC_TABLE + ";";
    private static final String SELECT_WORD = "SELECT * " +
                                              "FROM " + WORD_TABLE +
                                              " WHERE id_word=?;";
    private static final String INSERT_WORD = "INSERT INTO " + WORD_TABLE + " VALUES (?);";

    private static final String DELETE_STMT = "DELETE FROM ?";

    private static final String INDEX_INSERT = "INSERT INTO " + INDEX_TABLE + " VALUES (?,?,?);";
    private static final String TF_IDF_INDEX_INSERT = "INSERT INTO " + INDEX_TF_IDF_TABLE + " VALUES (?,?,?);";

    // For efficiency store the most used prepared statements.
    private PreparedStatement storeWordPrepStmt;
    private PreparedStatement checkWordPrepStmt;
    private PreparedStatement tfPrepStmt;
    private PreparedStatement tfIdfPrepStmt;

    // Connection to the database.
    private Connection connection;

    /**
     * Establishes a connection to the database and builds the most used
     * prepared statements for efficiency.
     */
    public DBLayer() {
        connection = ConnectionBuilder.getConnection();

        try {
            storeWordPrepStmt = connection.prepareStatement(INSERT_WORD);
            checkWordPrepStmt = connection.prepareStatement(SELECT_WORD);
            tfPrepStmt = connection.prepareStatement(INDEX_INSERT);
            tfIdfPrepStmt = connection.prepareStatement(TF_IDF_INDEX_INSERT);
        } catch (SQLException e) {
            LOGGER.error("Could not create prepared statements.",e);
        }
    }

    /**
     * Stores the name of the document in the database.
     * @param idDocument ID of the document to store
     * @param documentName name of the document to store.
     * @return true if the document was stored.
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

    /**
     * Gets the number of documents in the database.
     * @return the number of documents.
     */
    public int getNumberOfDocuments(){

        LOGGER.trace("Getting the number of docs");

        try {
            PreparedStatement prepstmt = connection.prepareStatement(SELECT_NUMBER_OF_DOCS);
            ResultSet rs = prepstmt.executeQuery();

            if(!rs.next()){
                return 0;
            }

            return rs.getInt(1);

        } catch (SQLException e) {
            LOGGER.fatal("There is not a table " + DOC_TABLE + " in the database.", e);
            return -1;
        }
    }

    /**
     * Checks if a word exists in the database.
     * @param word the word to check.
     */
    private boolean wordExists(String word) throws SQLException {

        checkWordPrepStmt.setString(1, word);
        ResultSet rs = checkWordPrepStmt.executeQuery();

        return rs.next();
    }

    /**
     * Stores a word in the database if it does not exist.
     * @param word the word to store.
     */
    public void storeWord(String word) {

        try {

            if (!wordExists(word)) {

                storeWordPrepStmt.setString(1, word);
                storeWordPrepStmt.executeUpdate();
            }

        } catch (SQLException e) {
            LOGGER.debug("Could not store word \"{}\" in the database, "
                    + "other thread might have stored it.", word);
        }
    }

    /**
     * Stores a word of a document using tf weight.
     * @param word word to store.
     * @param document document associated to the word.
     * @param weight weight associated to the word.
     */
    public void storeInverseTfEntry(String word, int document, float weight) {
        storeInverseEntry(word, document, weight, tfPrepStmt);
    }

    /**
     * Stores a word of a document using tf-idf weight.
     * @param word word to store.
     * @param document document associated to the word.
     * @param weight weight associated to the word.
     */
    public void storeInverseTfIdfEntry(String word, int document, float weight) {
        storeInverseEntry(word, document, weight, tfIdfPrepStmt);
    }

    /**
     * Stores a word of a document using some weight.
     * @param word word to store.
     * @param document document associated to the word.
     * @param weight weight associated to the word.
     * @param prepStmt the table it is going to insert the word.
     */
    private void storeInverseEntry(String word, int document,
            float weight, PreparedStatement prepStmt) {

        try {
            prepStmt.setString(1, word);
            prepStmt.setInt(2, document);
            prepStmt.setFloat(3, weight);
            prepStmt.execute();

        } catch (SQLException e) {
            LOGGER.error("Could not store index in the database.",e);
        }
    }

    /**
     * Gets the list of relevant documents with tf weights associated to a word.
     * @param word searches in the database documents containing this String.
     * @return the list of relevant documents with tf weights associated.
     * @see RelevantDocument
     */
    public List<RelevantDocument> getRelevantDocsTf(String word) {

        LOGGER.trace("Getting relevant documents of the word {} using tf.", word);
        return getRelevantDocs(word,INDEX_TABLE);
    }

    /**
     * Gets the list of relevant documents with tf-idf weights associated to a word.
     * @param word searches in the database documents containing this String.
     * @return the list of relevant documents with tf-idf weights associated.
     * @see RelevantDocument
     */
    public List<RelevantDocument> getRelevantDocsTfIdf(String word) {

        LOGGER.trace("Getting relevant documents of the word {} using tf-idf.", word);
        return getRelevantDocs(word,INDEX_TF_IDF_TABLE);
    }

    /**
     * Gets the list of relevant documents associated to a word.
     * @param word searches in the database documents containing this String.
     * @param table where the documents will be found.
     * @return the list of relevant documents n the table.
     * @see RelevantDocument
     */
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

    /**
     * Builds a list of relevant documents using a ResultSet;
     * @param rs the ResultSet to build the list from.
     * @return the list of relevant documents built.
     * @throws SQLException if there is a problem with the ResultSet.
     * @see RelevantDocument
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

    /**
     * Deletes the information in the database.
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

    /**
     * Closes the connection of the database.
     */
    public void finalize(){

        try {
            connection.close();
        } catch (SQLException e) {
            LOGGER.error("A problem occurred closing the connection of the database.",
                    e);
        }
    }

}
