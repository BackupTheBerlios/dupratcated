package fr.umlv.symphonie.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;



public class ConnectionManager {
    
    static private Connection connection = null;
    static private boolean driversLoaded = false;
    
    public static Connection createConnection() {
        

        String url = "jdbc:postgresql://213.245.132.210";
        String base = "symphonie";
        String login = "symphonie";
        String password = "dupratcated";
        
    	DriverLoader.loadDrivers();
      
     
    	
    	if (connection == null) try {
        connection = DriverManager.getConnection(url + "/" + base, login, password);
      } catch (SQLException e) {  
          System.out.println("error : createConnection()\n");
          e.printStackTrace();
      }
    	
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

