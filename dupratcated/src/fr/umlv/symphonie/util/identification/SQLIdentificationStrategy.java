/*
 * This file is part of Symphonie
 * Created : 20 mars 2005 20:05:51
 */

package fr.umlv.symphonie.util.identification;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import fr.umlv.symphonie.data.ConnectionManager;

/**
 * SQL based implementation of an <code>IdentificationStrategy</code>
 */
public final class SQLIdentificationStrategy extends
    AbstractIndentificationStrategy {

  // ----------------------------------------------------------------------------
  // Static fields
  // ---------------------------------------------------------------------------

  /** The table where the passwords are stored */
  protected static final String PASSWORD_TABLE = "root_pass";

  /** The table column that contains password */
  protected static final String PASSWORD_COLUMN = "password";

  /** The where clause for password */
  private static final String WHERE_CLAUSE = " WHERE " + PASSWORD_COLUMN + "='";

  /** Request that verifies password */
  private static final String PASSWORD_REQUEST = "SELECT " + PASSWORD_COLUMN
      + " FROM " + PASSWORD_TABLE;

  /** Request that updates password */
  private static final String PASSWORD_UPDATE = "UPDATE " + PASSWORD_TABLE
      + " SET " + PASSWORD_COLUMN + "='";

  /** The table where the passwords are stored */
  protected static final String LOCK_TABLE = "login_lock";

  /** The table column that contains password */
  protected static final String LOCK_COLUMN = "is_logged";

  /** Request that verifies lock */
  private static final String LOCK_TEST = "SELECT count(" + LOCK_COLUMN
      + ") FROM " + LOCK_TABLE + " WHERE " + LOCK_COLUMN + " = 1 ;";

  /** Request that takes the lock */
  private static final String LOCK_REQUEST = "INSERT INTO " + LOCK_TABLE
      + " VALUES(1) ;";

  /** Request that releases the lock */
  private static final String LOCK_RELEASE = "DELETE FROM " + LOCK_TABLE
      + " WHERE " + LOCK_COLUMN + "= 1;";

  // ----------------------------------------------------------------------------
  // Constructors
  // ---------------------------------------------------------------------------

  /**
   * Default constructor, creates an <code>SQLIdentificationStrategy</code>
   * using a connection from the <code>ConnectionManager</code>
   * 
   * @see ConnectionManager
   */
  public SQLIdentificationStrategy() {
    this(ConnectionManager.createConnection());
  }

  /**
   * Creates an <code>SQLIdentificationStrategy</code> with the given SQL
   * connection
   * 
   * @param con
   *          The database connection
   */
  public SQLIdentificationStrategy(Connection con) {
    this.con = con;
  }

  // ----------------------------------------------------------------------------
  // Private members
  // ---------------------------------------------------------------------------

  /** Login status */
  private boolean loggedIn;

  /** SQL Connection */
  private Connection con;

  /**
   * Test whether a password is valid or not
   * 
   * @param pwd
   *          The password to test
   * @return <code>true</code> if password is valid, <code>false</code>
   *         otherwise
   * @throws IdentificationException
   *           If an SQLException occurs
   */
  private final boolean isValidPassword(String pwd)
      throws IdentificationException {
    try {
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(PASSWORD_REQUEST + WHERE_CLAUSE + pwd
          + "' ;");
      if (rs.next()) {
        return true;
      }
    } catch (SQLException e) {
      throw new IdentificationException("Unable to verify password validity : "
          + e.getSQLState(), e);
    }
    return false;
  }

  /**
   * Tries to get the login lock
   * 
   * @return <code>true</code> if succeded or <code>false</code> if someone
   *         else has got the lock
   * @throws IdentificationException
   *           If there's an SQLException
   */
  private final boolean getLock() throws IdentificationException {
    try {
      Statement stmt = con.createStatement();
      ResultSet rs = stmt.executeQuery(LOCK_TEST);
      rs.next();
      int count = rs.getInt(1);
      if (count == 0) {
        stmt.executeUpdate(LOCK_REQUEST);
        return true;
      } else
        return false;
    } catch (SQLException e) {
      throw new IdentificationException("Unable to get login lock : "
          + e.getSQLState(), e);
    }
  }

  /**
   * Release the login lock. If not logged in, method does nothing.
   * 
   * @throws IdentificationException
   *           If there's an SQLException
   */
  private final void releaseLock() throws IdentificationException {
    try {
      if (loggedIn) {
        Statement stmt = con.createStatement();
        stmt.executeUpdate(LOCK_RELEASE);
      }
    } catch (SQLException e) {
      throw new IdentificationException(
          "Unable to release login lock(database needs to be updated manually) : "
              + e.getSQLState(), e);
    }
  }

  // ----------------------------------------------------------------------------
  // IdentificationStrategy implementation
  // ---------------------------------------------------------------------------

  public void identify(String password) throws IdentificationException {
    if (!isValidPassword(password))
      throw new IdentificationException("Invalid password");
    loggedIn = getLock();
    fireStateChanged();
  }

  /**
   * Throws an <code>UnsupportedOperationException</code>
   */
  public void identifyNow(String password) {
    throw new UnsupportedOperationException(
        "Implementation doesn't support non blocking identification");
  }

  public boolean isIdentified() {
    return loggedIn;
  }

  public void logout() throws IdentificationException {
    releaseLock();
    loggedIn = false;
    fireStateChanged();
  }

  public void changePassword(String oldPassword, String newPassword)
      throws IdentificationException {

    if (!loggedIn)
      throw new IdentificationException(
          "You must be identified in order to change password");

    if (isValidPassword(oldPassword)) {
      try {
        Statement stmt = con.createStatement();
        stmt.executeUpdate(PASSWORD_UPDATE + newPassword + '\'' + WHERE_CLAUSE
            + oldPassword + "';");
      } catch (SQLException e) {
        throw new IdentificationException("Unable to change password", e);
      }
    } else
      throw new IdentificationException("Invalid old password :" + oldPassword);
    logout();
  }
}
