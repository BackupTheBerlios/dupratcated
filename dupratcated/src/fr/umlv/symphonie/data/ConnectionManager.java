
package fr.umlv.symphonie.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JOptionPane;

import fr.umlv.symphonie.util.SymphoniePreferencesManager;
import fr.umlv.symphonie.view.DatabaseDialog;

public class ConnectionManager {

  static private Connection connection = null;
  static private boolean driversLoaded = false;

  /**
   * Opens a dialog asking for DB connection parameter
   * 
   * @return The parameters as a set of properties
   */
  private static final Properties openDialog() {
    DatabaseDialog dbd = new DatabaseDialog(null);
    dbd.setVisible(true);
    return dbd.getUserInput();
  }

  public static Connection createConnection() {

    DriverLoader.loadDrivers();

    Properties conProps = SymphoniePreferencesManager.getDBProperties();
    if (conProps == null) {
      JOptionPane
          .showMessageDialog(
              null,
              "This seem to be the first time you use Symphonie.\nBefore starting please fill the DB info, it will be only asked once.");
      conProps = openDialog();
    }

    if (conProps.isEmpty()) {
      System.out
          .println("Cannot start main program without a database connection");
      System.exit(1);
    }

    if (connection == null) {
      try {
        connection = DriverManager.getConnection(conProps.getProperty("url"),
            conProps);
      } catch (SQLException e) {
        System.out.println("Error while connecting to database : \n"
            + e.getMessage());
        System.out.println(e.getSQLState());
        System.exit(1);
      }
    }
    return connection;
  }

  public static void closeConnection() {
    if (connection != null) try {
      connection.close();
      connection = null;
    } catch (SQLException e) {
      System.out.println("Error closing connection : " + e.getMessage());
      System.out.println(e.getSQLState());
    }
  }
}
