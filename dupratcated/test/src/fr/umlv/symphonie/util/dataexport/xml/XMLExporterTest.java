/*
 * This file is part of Symphonie Created : 2 mars 2005 17:48:49
 */

package fr.umlv.symphonie.util.dataexport.xml;

import junit.framework.TestCase;
import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.util.dataexport.DataExporter;
import fr.umlv.symphonie.util.dataexport.xml.XMLExporter;

/**
 * @author Laurent GARCIA
 */
public class XMLExporterTest extends TestCase {

  DataExporter de = new XMLExporter();

  /**
   * A SUPRIMER c'est juste pour tester pour nous. Mettre la dtd à la racine
   * pour le moment, utiliser la bdd de sus, les fichiers exportés seront à la
   * racine pour le moment
   */
  public void testExportStudentView() {
    Student s = new Student(3, "Laurent", "Garcia", "sale homo !");
    de.exportStudentView("student_view.xml", s);
  }

  public void testExportTeacherView() {
    Course c = new Course(0, "Java", (float) 0.5);
    de.exportTeacherView("teacher_view.xml", c);
  }

  public void testExportJuryView() {
    de.exportJuryView("jury_view.xml");
  }

}
