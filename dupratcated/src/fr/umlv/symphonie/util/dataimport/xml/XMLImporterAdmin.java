/*
 * This file is part of Symphonie Created : 9 mars 2005 14:00:00
 */

package fr.umlv.symphonie.util.dataimport.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.util.dataimport.DataImporterException;

/**
 * @author Laurent GARCIA
 */
public class XMLImporterAdmin extends XMLImporter {

  /*
   * (non-Javadoc)
   * 
   * @see fr.umlv.symphonie.util.dataimport.DataImporter#importStudentView(java.lang.String)
   */
  public void importStudentView(String documentName, DataManager dm)
      throws DataImporterException {
    HashMap<Course, Map<Integer, StudentMark>> studentViewMap;
    Document d = newDocument(documentName);
    Element root = d.getDocumentElement();

    /** if the document isn't a xml student view we stop */
    if (!root.getAttribute("view").equals("student")) {
      throw new DataImporterException("the file isn't a student view.\n");
    }

    final Map<Integer, Course> courseMap = getCourseNodes(root);
    final Map<Integer, Mark> markMap = getMarkNodes(root, courseMap);
    final Map<Student, Map<Integer, StudentMark>> studentAndStudentMakMap = getStudentNodes(
        root, false, markMap);

    /** we add and update all the course data */
    for (Course c : courseMap.values()) {
      try {
        /** if we need to add */
        if (c.getId() == -1) {
          dm.addCourse(c.getTitle(), c.getCoeff());
        } else {
          /** else we update */
          dm.changeCourseTitle(c, c.getTitle());
        }
      } catch (DataManagerException e) {
        throw new DataImporterException(
            "Error during the importation with the bdd.\n", e);
      }
    }

    /** we add and update all the mark data */
    for (Mark m : markMap.values()) {
      try {
        /** if we need to add */
        if (m.getId() == -1) {
          dm.addMark(m.getDesc(), m.getCoeff(), m.getCourse());
        } else {
          /** else we update */
          dm.changeMarkDescriptionAndCoeff(m, m.getDesc(), m.getCoeff());
        }
      } catch (DataManagerException e) {
        throw new DataImporterException(
            "Error during the importation with the bdd.\n", e);
      }
    }

    /** we update all the student mark data */
    for (Map<Integer, StudentMark> tmp : studentAndStudentMakMap.values()) {
      for (StudentMark sm : tmp.values()) {
        try {
          dm.changeStudentMarkValue(sm, sm.getValue());
        } catch (DataManagerException e) {
          throw new DataImporterException(
              "Error during the importation with the bdd.\n", e);
        }
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.umlv.symphonie.util.dataimport.DataImporter#importTeacherView(java.lang.String)
   */
  public void importTeacherView(String documentName, DataManager dm)
      throws DataImporterException {
    Document d = newDocument(documentName);
    Element root = d.getDocumentElement();

    if (!root.getAttribute("view").equals("teacher")) {
      throw new DataImporterException("the file isn't a teacher view.\n");
    }

    final Map<Integer, Course> courseMap = getCourseNodes(root);
    final Map<Integer, Mark> markMap = getMarkNodes(root, courseMap);
    final Map<Student, Map<Integer, StudentMark>> studentAndStudentMakMap = getStudentNodes(
        root, false, markMap);
    final List<Formula> list = getFormulaNodes(root);
    Course course = null;

    /** there is only one course node since we are in the teacher view */
    if (courseMap.size() != 1) {
      throw new DataImporterException("there can be only one course node.\n");
    }

    /**
     * we get the course object (there is only one since we are in the teacher
     * view)
     */
    for (Course c : courseMap.values()) {
      course = c;
    }

    if (list == null) {
      throw new DataImporterException(
          "there is an error with your formula(s).\n");
    }

    /** we add and update all the mark data */
    for (Mark m : markMap.values()) {
      try {
        /** if we need to add */
        if (m.getId() == -1) {
          dm.addMark(m.getDesc(), m.getCoeff(), m.getCourse());
        } else {
          /** else we update */
          dm.changeMarkDescriptionAndCoeff(m, m.getDesc(), m.getCoeff());
        }
      } catch (DataManagerException e) {
        throw new DataImporterException(
            "Error during the importation with the bdd.\n", e);
      }
    }

    /** we update all the student data */
    for (Student s : studentAndStudentMakMap.keySet()) {
      try {
        /** if we need to add */
        if (s.getId() == -1) {
          dm.addStudent(s.getName(), s.getLastName());
        } else {
          /** else we update */
          dm.changeStudentNameAndLastNameAndComment(s, s.getName(), s
              .getLastName(), null);
        }
      } catch (DataManagerException e) {
        throw new DataImporterException(
            "Error during the importation with the bdd.\n", e);
      }
    }

    /** we update all the student mark data */
    for (Map<Integer, StudentMark> tmp : studentAndStudentMakMap.values()) {
      for (StudentMark sm : tmp.values()) {
        try {
          dm.changeStudentMarkValue(sm, sm.getValue());
        } catch (DataManagerException e) {
          throw new DataImporterException(
              "Error during the importation with the bdd.\n", e);
        }
      }
    }

    /** we update the formula data */
    for (Formula f : list) {
      try {
        /** if we need to add */
        if (f.getID() == -1) {
          dm.addTeacherFormula(null, f.getDescription(), course, f.getColumn());
        } else {
          /** else we update */
          // dm.change();
        }
      } catch (DataManagerException e) {
        throw new DataImporterException(
            "Error during the importation with the bdd.\n", e);
      }
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.umlv.symphonie.util.dataimport.DataImporter#importJuryView(java.lang.String)
   */
  public void importJuryView(String documentName, DataManager dm)
      throws DataImporterException {
    Document d = newDocument(documentName);
    Element root = d.getDocumentElement();

    if (!root.getAttribute("view").equals("jury")) {
      throw new DataImporterException("the file isn't a jury view.\n");
    }

    final Map<Integer, Course> courseMap = getCourseNodes(root);
    final Map<Integer, Mark> markMap = getMarkNodes(root, courseMap);
    final Map<Student, Map<Integer, StudentMark>> studentAndStudentMakMap = getStudentNodes(
        root, true, markMap);
    final List<Formula> list = getFormulaNodes(root);

    if (list == null) {
      throw new DataImporterException(
          "there is an error with your formula(s).\n");
    }

    /** we add and update all the course data */
    for (Course c : courseMap.values()) {
      try {
        /** if we need to add */
        if (c.getId() == -1) {
          dm.addCourse(c.getTitle(), c.getCoeff());
        } else {
          /** else we update */
          dm.changeCourseTitleAndCoeff(c, c.getTitle(), c.getCoeff());
        }
      } catch (DataManagerException e) {
        throw new DataImporterException(
            "Error during the importation with the bdd.\n", e);
      }
    }

    /** we update all the student data */
    for (Student s : studentAndStudentMakMap.keySet()) {
      try {
        /** if we need to add */
        if (s.getId() == -1) {
          dm.addStudent(s.getName(), s.getLastName());
          dm.changeStudentComment(s, s.getComment());
        } else {
          /** else we update */
          dm.changeStudentNameAndLastNameAndComment(s, s.getName(), s
              .getLastName(), s.getComment());
        }
      } catch (DataManagerException e) {
        throw new DataImporterException(
            "Error during the importation with the bdd.\n", e);
      }
    }

    /** we update the formula data */
    for (Formula f : list) {
      try {
        /** if we need to add */
        if (f.getID() == -1) {
          dm.addJuryFormula(null, f.getDescription(), f.getColumn());
        } else {
          /** else we update */
          // dm.change();
        }
      } catch (DataManagerException e) {
        throw new DataImporterException(
            "Error during the importation with the bdd.\n", e);
      }
    }
  }
}
