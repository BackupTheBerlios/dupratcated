/*
 * This file is part of Symphonie
 * Created : 20 mars 2005 17:00:44
 */
package fr.umlv.symphonie.util;

import java.util.Properties;

import junit.framework.TestCase;
import static fr.umlv.symphonie.util.SymphoniePreferencesManager.*;
import fr.umlv.symphonie.util.SymphoniePreferencesManager.DataBaseType;
import fr.umlv.symphonie.view.Symphonie;


/**
 * Preferences test cases
 * @author PEÑA SALDARRIAGA Sébastian
 */
public class TestSymphoniePreferencesManager extends TestCase {

  public void testSetLanguage() {
    setLanguage(Symphonie.Language.SPANISH);
  }

  public void testGetLanguage() {
    Symphonie.Language l = getLanguage();
    System.out.println("Stored language : " + l);
    assertEquals(l, Symphonie.Language.SPANISH);
  }

  public void testSetDBType() {
    setDBType(DataBaseType.PostgreSQL);
  }

  public void testGetDBType() {
    DataBaseType dbt = getDBType();
    System.out.println("Stored db type : " + dbt);
    assertEquals(dbt, DataBaseType.PostgreSQL);
  }

  public void testSetDBProperties() {
    Properties props = new Properties();
    props.setProperty(DB_HOST, "localhost");
    props.setProperty(DB_NAME, "symphonie");
    props.setProperty(DB_USER, "dummy");
    props.setProperty(DB_PASS, "xxxxx");
    setDBProperties(props);
  }

  public void testGetDBProperties() {
    String expectedURL = "jdbc:postgresql://localhost/symphonie";
    Properties props = getDBProperties();
    System.out.println("Connection properties " + props);
    assertEquals(expectedURL, props.getProperty("url"));
  }
  
  public void testClearPreferences() {
    clearPreferences();
  }
}
