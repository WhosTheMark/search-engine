package model;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBDriver {

    private static final Logger LOGGER = Logger.getLogger(DBDriver.class.getName());
    private static final Connection CONNECTION = ConnectionBuilder.getConnection();
    private static final String WORD_TABLE = "word";
    private static final String INDEX_TABLE = "indx";
    private static final String DOC_TABLE = "document";

    // To avoid instantiation
    private DBDriver() {
    }

    /*
     * Store a document in the database.
     */
    public static void storeDocument(int idDocument, File document) {

        LOGGER.log(Level.FINE, "Storing document " + document.getName());

        try {

            String strStmt = "INSERT INTO " + DOC_TABLE + " VALUES (?,?);";
            PreparedStatement prepstmt = CONNECTION.prepareStatement(strStmt);
            prepstmt.setInt(1, idDocument);
            prepstmt.setString(2, document.getName());
            prepstmt.executeUpdate();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not add a document to database.", e);
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
    private static void storeWord(String word) {

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
    private static void insertEntry(String word, int document, int weight) {

        LOGGER.log(Level.FINER, "Inserting index entry with word: " + word
                + " document id: " + document + " and weight: " + weight);

        String strStmt = "INSERT INTO " + INDEX_TABLE + " VALUES (?,?,?);";

        try {

            PreparedStatement prepstmt = CONNECTION.prepareStatement(strStmt);
            prepstmt.setString(1, word);
            prepstmt.setInt(2, document);
            prepstmt.setInt(3, weight);
            prepstmt.execute();

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Could not store index in the database.",
                    e);
        }
    }

    /*
     * Store an inverse file in the database
     */
    public static void storeInverseFile(Map<String, Integer> inverseFile,
            int document) {

        LOGGER.log(Level.FINE, "Storing inverse file for document with id: "
                + document);

        for (Entry<String, Integer> elem : inverseFile.entrySet()) {
            storeWord(elem.getKey());
            insertEntry(elem.getKey(), document, elem.getValue());
        }

    }

    /*
     * Returns the list of relevant document of a word, the list is ordered in the
     * same way documents are ordered in the database.
     */
    public static List<RelevantDocument> getRelevantDocs(String word) {

        LOGGER.log(Level.FINE, "Getting relevant documents of the word " + word);

        String strQuery = "SELECT * FROM " + INDEX_TABLE + " , " + DOC_TABLE
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

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE,
                    "A problem occurred ereasing the tables of the database.",
                    e);
        }

    }

}
