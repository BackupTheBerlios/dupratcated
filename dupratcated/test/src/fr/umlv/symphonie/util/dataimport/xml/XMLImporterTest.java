/*
 * This file is part of Symphonie Created : 8 mars 2005 21:52:16
 */

package fr.umlv.symphonie.util.dataimport.xml;

import junit.framework.TestCase;
import fr.umlv.symphonie.util.dataimport.DataImporter;
import fr.umlv.symphonie.util.dataimport.DataImporterException;
import fr.umlv.symphonie.util.dataimportation.xml.XMLImporter;

/**
 * @author Laurent GARCIA
 */
public class XMLImporterTest extends TestCase {

  DataImporter di = new XMLImporter();

  public void testImportStudentView() {
    try {
      di.importStudentView("student_view.xml");
    } catch (DataImporterException e) {
      e.printStackTrace();
    }
  }
}
