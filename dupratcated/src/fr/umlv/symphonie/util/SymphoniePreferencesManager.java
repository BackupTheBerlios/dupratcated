/*
 * This file is part of Symphonie
 * Created : 20 mars 2005 15:37:26
 */

package fr.umlv.symphonie.util;

import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import fr.umlv.symphonie.view.Symphonie;

/**
 * This class consists on a set of static methods for storing Symphonie
 * preferences
 */
public final class SymphoniePreferencesManager {

  /**
   * Sole constructor
   */
  private SymphoniePreferencesManager() {
  }

  // ---------------------------------------------------------------------------
  // Supported Data bases
  // ---------------------------------------------------------------------------

  /**
   * Enum defines the supported JDBC database types supported by symphonie.
   */
  public enum DataBaseType {
    PostgreSQL {

      public String getSubprotcol() {
        return "postgresql";
      }

      public String getDriverName() {
        return "org.postgresql.Driver";
      }
    },
    MySQL {

      public String getSubprotcol() {
        return "mysql";
      }

      public String getDriverName() {
        return "com.mysql.jdbc.Driver";
      }
    };

    /**
     * Returns the specific database subprotocol name. <br>
     * For jdbc:trucdeouf://127.0.0.1/db_name, method must return "trucdeouf".
     * 
     * @return The subprotocol name
     */
    public abstract String getSubprotcol();

    /**
     * Returns the specific database driver class fully qualified name ex: <br>
     * &nbsp;&nbsp;&nbsp;org.trucdeouf.TaxiDriver
     * 
     * @return The driver name
     */
    public abstract String getDriverName();

    /**
     * Attempts to load the db driver by calling
     * <code>Class.forName(getDriverName())</code>
     * 
     * @throws ClassNotFoundException
     *           If the driver class can't be found
     */
    public void loadDriver() throws ClassNotFoundException {
      Class.forName(getDriverName());
    }
  }

  // ---------------------------------------------------------------------------
  // Private static fields
  // ---------------------------------------------------------------------------

  /** Symphonie preferences node name */
  private static final String NODE_NAME = "dupratcated";

  /** Symphonie user preferences */
  private static Preferences symphoniePrefs;

  // ---------------------------------------------------------------------------
  // Private methods
  // ---------------------------------------------------------------------------

  /**
   * Returns the current preferences node, if it doesn't exist it will be
   * created.
   * 
   * @return The <code>Preferences</code> node
   */
  private static final Preferences getNode() {
    if (symphoniePrefs == null)
      symphoniePrefs = Preferences.userRoot().node(NODE_NAME);
    return symphoniePrefs;
  }

  /**
   * Test whether the given key maps an internal property.
   * 
   * @param key
   *          The key to test
   * @return <code>true</code> if the key overrides an internal property
   */
  private static final boolean isInternalProperty(String key) {
    return key.startsWith("symphdb.") || key.equals(LANGUAGE);
  }

  // ---------------------------------------------------------------------------
  // Public attributes
  // ---------------------------------------------------------------------------

  public static final String DB_TYPE = "symphdb.type";
  public static final String DB_HOST = "symphdb.host";
  public static final String DB_PORT = "symphdb.port";
  public static final String DB_NAME = "symphdb.name";
  public static final String DB_USER = "symphdb.user";
  public static final String DB_PASS = "symphdb.pass";

  public static final String LANGUAGE = "symphonie.language";

  // ---------------------------------------------------------------------------
  // Public interface
  // ---------------------------------------------------------------------------

  /**
   * Erases all data in Symphonie preferences
   */
  public static final void clearPreferences() {
    try {
      getNode().clear();
    } catch (BackingStoreException e) {
      throw new RuntimeException(
          "Lame Preferences implementation Exception : ", e);
    }
  }

  /**
   * Sets the given language a preferred
   * 
   * @param lang
   *          The language to set
   */
  public static final void setLanguage(Symphonie.Language lang) {
    getNode().put(LANGUAGE, lang.name());
  }

  /**
   * Attemps to retrieve the language stored in the preferences
   * 
   * @return The language retrieven, or French if there wasn't any language
   *         stored.
   */
  public static final Symphonie.Language getLanguage() {
    String l = getNode().get(LANGUAGE, null);
    if (l == null) {
      l = Symphonie.Language.FRENCH.name();
      setLanguage(Symphonie.Language.FRENCH);
    }
    return Symphonie.Language.valueOf(l);
  }

  /**
   * Sets the given type a preferred
   * 
   * @param dbt
   *          The database type
   */
  public static final void setDBType(DataBaseType dbt) {
    getNode().put(DB_TYPE, dbt.name());
  }

  /**
   * Attemps to retrieve the database type stored in the preferences
   * 
   * @return The type retrieven, or null if there wasn't one.
   */
  public static final DataBaseType getDBType() {
    String l = getNode().get(DB_TYPE, null);
    return (l != null) ? DataBaseType.valueOf(l) : null;
  }

  /**
   * Puts the db properties into the preferences, where properties are of String
   * type and keys can be one of :<br>
   * <code>DB_HOST</code>,<code>DB_PORT</code>,<code>DB_NAME</code>,
   * <code>DB_USER</code>,<code>DB_PASS</code><br>
   * Database type cannot be set with these routine.
   * 
   * @see #setDBType(DataBaseType)
   * @param props
   *          The properties to put
   */
  public static final void setDBProperties(Properties props) {
    Preferences prefs = getNode();
    String prop = props.getProperty(DB_HOST);
    if (prop != null) prefs.put(DB_HOST, prop);

    prop = props.getProperty(DB_PORT);
    if (prop != null) prefs.put(DB_PORT, prop);

    prop = props.getProperty(DB_NAME);
    if (prop != null) prefs.put(DB_NAME, prop);

    prop = props.getProperty(DB_USER);
    if (prop != null) prefs.put(DB_USER, prop);

    prop = props.getProperty(DB_PASS);
    if (prop != null) prefs.put(DB_PASS, prop);
  }

  /**
   * Convenient method for retrieving a ready-to-use-for-connection set of
   * properties <br>. The returned object contains 3 properties :
   * <code>"url"</code>,<code>"user"</code>,<code>"password"</code>.
   * 
   * @see #putCustomProperty(String, String)
   * @see #getCustomProperty(String)
   * @return a <code>Properties</code> object or <code>null</code> if at
   *         least one of the data base properties can't be found.
   */
  public static final Properties getDBProperties() {

    Preferences prefs = getNode();

    String user = prefs.get(DB_USER, null);
    if (user == null) return null;

    String pass = prefs.get(DB_PASS, null);
    if (pass == null) return null;

    DataBaseType t = getDBType();
    if (t == null) return null;

    StringBuilder url = new StringBuilder("jdbc:");
    url.append(t.getSubprotcol()).append("://");

    String tmp = prefs.get(DB_HOST, null);
    if (tmp == null) return null;
    url.append(tmp);

    tmp = prefs.get(DB_PORT, null);
    if (tmp != null) url.append(':').append(tmp);

    tmp = prefs.get(DB_NAME, null);
    if (tmp == null) return null;
    url.append('/').append(tmp);

    Properties props = new Properties();
    props.setProperty("url", url.toString());
    props.setProperty("user", user);
    props.setProperty("password", pass);

    return props;
  }

  /**
   * Puts a custome property into the preferences
   * 
   * @param key
   *          The key
   * @param value
   *          The value
   * @throws IllegalArgumentException
   *           If the key maps an internal value
   */
  public static final void putCustomProperty(String key, String value) {
    if (isInternalProperty(key))
      throw new IllegalArgumentException("Custom key: " + key
          + " overrides an internal key.");
    getNode().put(key, value);
  }

  /**
   * Gets a custom preferences property
   * 
   * @param key
   *          The property key
   * @return The property or <code>null</code> if it doesn't exist
   * @throws IllegalArgumentException
   *           If the key maps an internal value
   */
  public static final String getCustomProperty(String key) {
    if (isInternalProperty(key))
      throw new IllegalArgumentException("Custom key: " + key
          + " maps an internal key.");
    return getNode().get(key, null);
  }
}
