package model.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.RelevantDocument;

public class DBDriver {

    private static final Logger LOGGER = Logger.getLogger(DBDriver.class.getName());
    private static final Connection CONNECTION = ConnectionBuilder.getConnection();
    private static final String WORD_TABLE = "word";
    private static final String INDEX_TABLE = "indx";
    private static final String INDEX_TF_IDF_TABLE = "tf_idf_index";
    private static final String DOC_TABLE = "document";

    // To avoid instantiation
    private DBDriver() {
    }

    /*
     * Store a document in the database.
     */
    public static boolean storeDocument(int idDocument, String documentName) {

        LOGGER.log(Level.FINE, "Storing document " + documentName);

        try {

            String strStmt = "INSERT INTO " + DOC_TABLE + " VALUES (?,?);";
            PreparedStatement prepstmt = CONNECTION.prepareStatement(strStmt);
            prepstmt.setInt(1, idDocument);
            prepstmt.setString(2, documentName);
            prepstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not add a document to database.", e);
            return false;
        }

        return true;
    }

    public static int getNumberOfDocuments(){

        LOGGER.log(Level.FINEST, "Getting the number of docs");

        String strQuery = "SELECT COUNT(*) FROM " + DOC_TABLE + ";";
        ResultSet rs;
        try {
            PreparedStatement prepstmt = CONNECTION.prepareStatement(strQuery);
            rs = prepstmt.executeQuery();

            if(!rs.next()){
                return 0;
            }

            return rs.getInt(1);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "There is not a table " + DOC_TABLE 
                    + " in the database.", e);
            return -1;
        }
    }
    /*
     * Checks if a wordExists in the database.
     */
    private static boolean wordExists(String word) throws SQLException {

        LOGGER.log(Level.FINEST, "Checking if word " + word
                + "exists in database.");

        String strQuery = "SELECT * FROM " + WORD_TABLE + " WHERE id_word=?;";
        PreparedStatement prepstmt = CONNECTION.prepareStatement(strQuery);
        prepstmt.setString(1, word);
        ResultSet rs = prepstmt.executeQuery();

        return rs.next();
    }

    /*
     * Store a word in the database.
     */
    public static void storeWord(String word) {

        LOGGER.log(Level.FINER, "Storing word " + word);

        try {

            if (!wordExists(word)) {

                String strStmt = "INSERT INTO " + WORD_TABLE + " VALUES (?);";
                PreparedStatement prepstmt = CONNECTION
                        .prepareStatement(strStmt);
                prepstmt.setString(1, word);
                prepstmt.executeUpdate();

            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not store a word in the database",
                    e);
        }
    }

    /*
     * Store an entry of the inverse file in the database.
     */
    public static void storeInverseTfEntry(String word, int document, float weight) {

        storeInverseEntry(word, document, weight, INDEX_TABLE);
    }

    public static void storeInverseTfIdfEntry(String word, int document, float weight) {
        storeInverseEntry(word, document, weight, INDEX_TF_IDF_TABLE);
    }

    private static void storeInverseEntry(String word, int document,
            float weight, String table) {
        LOGGER.log(Level.FINER, "Inserting index entry with word: " + word
                + " document id: " + document + " and weight: " + weight);

        String strStmt = "INSERT INTO " + table + " VALUES (?,?,?);";

        try {

            PreparedStatement prepstmt = CONNECTION.prepareStatement(strStmt);
            prepstmt.setString(1, word);
            prepstmt.setInt(2, document);
            prepstmt.setFloat(3, weight);
            prepstmt.execute();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not store index in the database.",
                    e);
        }
    }

    /*
     * Returns the list of relevant document of a word using Term Frequency.
     */
    public static List<RelevantDocument> getRelevantDocsTf(String word) {

        LOGGER.log(Level.FINE, "Getting relevant documents of the word " + word);

        List<RelevantDocument> list = getRelevantDocs(word,INDEX_TABLE);
        return list;
    }

    /*
     * Returns the list of relevant document of a word using tf-idf.
     */
    public static List<RelevantDocument> getRelevantDocsTfIdf(String word) {

        LOGGER.log(Level.FINE, "Getting relevant documents of the word " + word);

        List<RelevantDocument> list = getRelevantDocs(word,INDEX_TF_IDF_TABLE);
        return list;
    }

    private static List<RelevantDocument> getRelevantDocs(String word, String table) {

        String strQuery = "SELECT * FROM " + table + " , " + DOC_TABLE
                + " WHERE id_document = document AND id_word=? "
                + "order by id_document;";
        PreparedStatement prepstmt;
        List<RelevantDocument> list;

        try {
            prepstmt = CONNECTION.prepareStatement(strQuery);
            prepstmt.setString(1, word);
            ResultSet rs = prepstmt.executeQuery();
            list = buildRelevantDocList(rs);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "A problem occurred getting the relevant documents of a word.",
                    e);
            list = new ArrayList<RelevantDocument>();
        }
        return list;
    }

    /*
     * Builds relevant document list using a result set.
     */
    private static List<RelevantDocument> buildRelevantDocList(ResultSet rs)
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
    public static void eraseDB() {

        LOGGER.log(Level.CONFIG, "Deleting info from databse.");

        String strDelete = "DELETE FROM ?";

        try {
            PreparedStatement prepstmt = CONNECTION.prepareStatement(strDelete);
            prepstmt.setString(1, INDEX_TABLE);
            prepstmt.executeUpdate();
            prepstmt.setString(1, WORD_TABLE);
            prepstmt.executeUpdate();
            prepstmt.setString(1, DOC_TABLE);
            prepstmt.executeUpdate();
            prepstmt.setString(1, INDEX_TF_IDF_TABLE);
            prepstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "A problem occurred ereasing the tables of the database.",
                    e);
        }

    }

}
