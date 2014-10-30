package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map.Entry;

public class DBDriver {

	private static Connection conn = ConnectionBuilder.getConnection();
	
	public static void storeInverseFile(HashMap<String, Integer> inverseFile, int document){
		
		for (Entry<String, Integer> elem : inverseFile.entrySet()){
			storeWord(elem.getKey());
			insertEntry(elem.getKey(), document, elem.getValue());			
		}
		
	}
	
	private static boolean wordExists(String word) throws SQLException{
		
		String strQuery = "SELECT * FROM word WHERE id_word=?;";
		PreparedStatement prepstmt = conn.prepareStatement(strQuery);
		prepstmt.setString(1, word);
		ResultSet rs = prepstmt.executeQuery();
		
		return rs.next();
	}
	
	private static void storeWord(String word){

		try {
			//If it does not exist then insert
			if (!wordExists(word)) {
				
				String strStmt = "INSERT INTO word VALUES (?);";
				PreparedStatement prepstmt = conn.prepareStatement(strStmt);
				prepstmt.setString(1, word);
				prepstmt.executeUpdate();
				
			}
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}		
	}
	
	private static void insertEntry(String word, int document, int weight) {
		
		String strStmt = "INSERT INTO indx VALUES (?,?,?);";
		
		try {
			
			PreparedStatement prepstmt = conn.prepareStatement(strStmt);
			prepstmt.setString(1, word);
			prepstmt.setInt(2, document);
			prepstmt.setInt(3, weight);
			prepstmt.execute();
			
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
}
