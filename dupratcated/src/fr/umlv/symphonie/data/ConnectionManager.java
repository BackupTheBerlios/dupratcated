package fr.umlv.symphonie.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class ConnectionManager {
    
    static private Connection connection = null;
    static private boolean driversLoaded = false;
    
    public static Connection createConnection() {
        
      String url = "jdbc:mysql://localhost";
    	String base = "dupratcated";
    	String login = "susmab";
    	String password = "";
        
    	DriverLoader.loadDrivers();

    	
    	if (connection == null) try {
        connection = DriverManager.getConnection(url + "/" + base, login,password);
      } catch (SQLException e) {
        base = "evazion";
        login = "root";
        
        try {
          connection = DriverManager.getConnection(url + "/" + base, login,
              password);
        } catch (SQLException e1) {
          System.out.println("error : createConnection()\n");
          e1.printStackTrace();
        }
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

