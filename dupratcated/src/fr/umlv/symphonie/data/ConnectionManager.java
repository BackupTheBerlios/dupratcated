package fr.umlv.symphonie.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class ConnectionManager {
    
    static private Connection connection = null;
    static private boolean driversLoaded = false;
    
    public static Connection createConnection() throws SQLException{
        
        String url = "jdbc:mysql://localhost";
    	String base = "dupratcated";
    	String login = "susmab";
    	String password = "";
        
    	DriverLoader.loadDrivers();

    	
    	if (connection == null)
    	    connection = DriverManager.getConnection(url + "/" + base, login,
password);
    	
        return connection;
    }
    
    public static void closeConnection(){
        
        if (connection != null)
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.out.println("Error closing connection");
                e.printStackTrace();
            }
    }
}

