package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionBuilder {
	
	/*change these as needed */
	private static String url = "jdbc:postgresql://localhost:5432/marcos";
	private static String user = "marcos";
	private static String pass = "12345678";
	
	//Get the connection to access the database
	public static Connection getConnection(){
		
		try{        
            Class.forName("org.postgresql.Driver");
            
        } catch (ClassNotFoundException e){
        
            System.out.println("JDBC driver is not installed.");
            return null;
        }
                
        Connection conexion;
        
        try{             
            conexion = DriverManager.getConnection(url,user,pass);
            
        } catch (SQLException e){
            
            System.out.println(e.getMessage());
            return null;   
        }
       
        return conexion; 
	}

}
