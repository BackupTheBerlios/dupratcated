
package fr.umlv.symphonie.data;

import fr.umlv.symphonie.util.SymphoniePreferencesManager.DataBaseType;

/**
 * A class designed for loading every database drivers included with the software.
 * @author susmab
 *
 */
public class DriverLoader {

  /**
   * Indicates if the drivers are already loaded or not.
   */
  private static boolean driversLoaded = false;

  /**
   * Private default constructor. 
   */
  private DriverLoader() {
  }

  /**
   * Loads all drivers available.
   */
  public static void loadDrivers() {

    if (driversLoaded == false) {
      for (DataBaseType t : DataBaseType.values())
        try {
          t.loadDriver();
        } catch (ClassNotFoundException e) {
          System.err.println("Unable to load Driver class : " + e.getMessage());
        }
      driversLoaded = true;
    }
  }
}
