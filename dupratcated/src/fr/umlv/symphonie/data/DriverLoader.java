
package fr.umlv.symphonie.data;

import fr.umlv.symphonie.util.SymphoniePreferencesManager.DataBaseType;

public class DriverLoader {

  private static boolean driversLoaded = false;

  private DriverLoader() {
  }

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
