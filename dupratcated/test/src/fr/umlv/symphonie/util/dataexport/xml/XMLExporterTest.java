/*
 * This file is part of Symphonie Created : 2 mars 2005 17:48:49
 */

package fr.umlv.symphonie.util.dataexport.xml;

import junit.framework.TestCase;
import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.util.dataexport.DataExporter;
import fr.umlv.symphonie.util.dataexport.DataExporterException;
import fr.umlv.symphonie.util.dataexport.xml.XMLExporter;

/**
 * @author Laurent GARCIA
 */
public class XMLExporterTest extends TestCase {

  final DataExporter de = new XMLExporter();
  final DataManager dm = new SQLDataManager();

  /**
   * A SUPRIMER c'est juste pour tester pour nous. Mettre la dtd � la racine
   * pour le moment, utiliser la bdd de sus, les fichiers export�s seront � la
   * racine pour le moment
   */
  public void testExportStudentView() {
    Student s = new Student(3, "Laurent", "Garcia", "sale homo !");
    try {
      de.exportStudentView("student_view.xml", dm, s);
    } catch (DataExporterException e) {
      e.printStackTrace();
    }
  }

  public void testExportTeacherView() {
    Course c = new Course(0, "Java", (float) 0.5);
    try {
      de.exportTeacherView("teacher_view.xml", dm, c);
    } catch (DataExporterException e) {
      e.printStackTrace();
    }
  }

  public void testExportJuryView() {
    try {
      de.exportJuryView("jury_view.xml", dm);
    } catch (DataExporterException e) {
      e.printStackTrace();
    }
  }

}
