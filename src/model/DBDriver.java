package model;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

public class DBDriver {

	private static final Connection conn = ConnectionBuilder.getConnection();
	private static final String wordTable = "word";
	private static final String indexTable = "indx";
	private static final String docTable = "document";
	
	
	public static void storeDocument(int idDocument, File document){
		
		try {
			
			String strStmt = "INSERT INTO " + docTable + " VALUES (?,?);";
			PreparedStatement prepstmt = conn.prepareStatement(strStmt);
			prepstmt.setInt(1, idDocument);
			prepstmt.setString(2,document.getName());
			prepstmt.executeUpdate();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}		
	}
	
	
	public static void storeInverseFile(HashMap<String, Integer> inverseFile, int document){
		
		for (Entry<String, Integer> elem : inverseFile.entrySet()){
			storeWord(elem.getKey());
			insertEntry(elem.getKey(), document, elem.getValue());			
		}
		
	}
	
	private static boolean wordExists(String word) throws SQLException{
		
		String strQuery = "SELECT * FROM " + wordTable + " WHERE id_word=?;";
		PreparedStatement prepstmt = conn.prepareStatement(strQuery);
		prepstmt.setString(1, word);
		ResultSet rs = prepstmt.executeQuery();
		
		return rs.next();
	}
	
	private static void storeWord(String word){

		try {
			//If it does not exist then insert
			if (!wordExists(word)) {
				
				String strStmt = "INSERT INTO " + wordTable + " VALUES (?);";
				PreparedStatement prepstmt = conn.prepareStatement(strStmt);
				prepstmt.setString(1, word);
				prepstmt.executeUpdate();
				
			}
			
		} catch (SQLException e) {
			System.out.println("ERROR storing the word: " + word);
			System.out.println(e.getMessage());
		}		
	}
	
	private static void insertEntry(String word, int document, int weight) {
		
		String strStmt = "INSERT INTO " + indexTable + " VALUES (?,?,?);";
		
		try {
			
			PreparedStatement prepstmt = conn.prepareStatement(strStmt);
			prepstmt.setString(1, word);
			prepstmt.setInt(2, document);
			prepstmt.setInt(3, weight);
			prepstmt.execute();
			
		} catch (SQLException e) {
			System.out.println("ERROR storing the index: " + word + " " + document);
			System.out.println(e.getMessage());
		}
	}
	
	
	/* 
	 * Returns the list of frequencies of a word, the list is 
	 * ordered in the same way documents are ordered in the
	 * database.
	 */
	public static List<Integer> getFreqs(String word){
		
		String strQuery = "SELECT * FROM " + indexTable + 
				" WHERE id_word=? ORDER BY document;";
		PreparedStatement prepstmt;
		
		try {
			prepstmt = conn.prepareStatement(strQuery);
			prepstmt.setString(1, word);
			ResultSet rs = prepstmt.executeQuery();
			ArrayList<Integer> list = new ArrayList<Integer>();
			
			while(rs.next()){
				list.add(rs.getInt("weight"));
			}
			
			return list;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static ArrayList<RelevanceOfDoc> getDocuments(){
		
		String strQuery = "SELECT * FROM " + docTable + 
				" WHERE ORDER BY id_document;";
		Statement stmt;
		
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(strQuery);
			ArrayList<RelevanceOfDoc> list = new ArrayList<RelevanceOfDoc>();
			
			while(rs.next()){
				
				int docId = rs.getInt("id_document");
				String docName = rs.getString("name");
				RelevanceOfDoc relevance = new RelevanceOfDoc(docId,docName);
				list.add(relevance);
			}
			
			return list;
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}
	
	public static void eraseDB(){
		
		String strDelete = "DELETE FROM ?";
		
		try {
			PreparedStatement prepstmt = conn.prepareStatement(strDelete);
			prepstmt.setString(1, indexTable);
			prepstmt.executeUpdate();
			prepstmt.setString(1, wordTable);
			prepstmt.executeUpdate();
			prepstmt.setString(1, docTable);
			prepstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}
