
package fr.umlv.symphonie.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.JOptionPane;

import fr.umlv.symphonie.util.SymphoniePreferencesManager;
import fr.umlv.symphonie.view.dialog.DatabaseDialog;

/**
 * A class which provides connection handling over a database. The connection in
 * Symphonie is a singleton, so the application does not have to connect many
 * times to the database server while running.
 * 
 * @author susmab
 * 
 */
public class ConnectionManager {

  /** The current connection. */
  static private Connection connection = null;

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

  /**
   * Creates a <code>Connection</code> if it has not already been earlier. In
   * this case, a dialog box appears on screen to ask parameters for connection.
   * The parameters are saved in Java <code>Properties</code> format, so the
   * next time the application is launched, the configuration part will be
   * skipped.
   * 
   * @return The <code>Connection</code> created, or an exisiting connection
   *         if it already exists.
   */
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

  /**
   * Closes the connection with the database. Should be called whenever the
   * program quits.
   */
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
