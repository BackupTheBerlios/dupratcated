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
      
      /*Properties props = new Properties();
      props.setProperty("user", login);
      props.setProperty("password", password);
      props.setProperty("ssl", "true");*/
    	
    	if (connection == null) try {
        connection = DriverManager.getConnection(url + "/" + base, login, password);
        System.out.println("on est connecte !");
      } catch (SQLException e) {
        base = "evazion";
        login = "root";
        e.printStackTrace();
        /*try {
          connection = DriverManager.getConnection(url + "/" + base,props);
        } catch (SQLException e1) {
          System.out.println("error : createConnection()\n");
          e1.printStackTrace();
        }*/
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

