/*
 * This file is part of Symphonie Created : 8 mars 2005 21:36:42
 */

package fr.umlv.symphonie.util.dataimportation.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.util.dataimport.DataImporter;

/**
 * @author Laurent GARCIA
 */
public class XMLImporter implements DataImporter {

  protected String documentName;
  protected final DataManager dm = new SQLDataManager();

  /**
   * @param root
   *          the root of the document
   * @return a map with all the courses
   */
  protected Map<Integer, Course> getCourseNodes(Element root) {
    final HashMap<Integer, Course> map = new HashMap<Integer, Course>();
    final NodeList nodes = root.getElementsByTagName("course");
    Course c;
    Node n;
    Element e;
    int id;

    /** for each course node */
    for (int i = 0; i < nodes.getLength(); i++) {
      /** we take a node */
      n = nodes.item(i);

      /** we create an element by using the course node */
      e = (Element) n;

      /** we get the attribute id_course from the element course */
      id = new Integer(e.getAttribute("id_course"));

      /** we create the course object */
      c = new Course(id, e.getElementsByTagName("title").item(0)
          .getTextContent(), Float.parseFloat(e.getElementsByTagName(
          "coeff_course").item(0).getTextContent()));

      /** we put the course object into the map */
      map.put(id, c);
    }

    return map;
  }

  /**
   * @param root
   *          the root of the document
   * @param courseMap
   *          a map with all the courses
   * @return a map with all the marks
   */
  protected Map<Integer, Mark> getMarkNodes(Element root,
      Map<Integer, Course> courseMap) {
    final HashMap<Integer, Mark> map = new HashMap<Integer, Mark>();
    final NodeList nodes = root.getElementsByTagName("examen");
    Mark m;
    Node n;
    Element e;
    int id;

    /** for each course node */
    for (int i = 0; i < nodes.getLength(); i++) {
      /** we take a node */
      n = nodes.item(i);

      /** we create an element by using the course node */
      e = (Element) n;

      /** we get the attribute id_examen from the element examen */
      id = new Integer(e.getAttribute("id_examen"));

      /** we create the mark object */
      m = new Mark(id, e.getElementsByTagName("desc").item(0).getTextContent(),
          Float.parseFloat(e.getElementsByTagName("coeff_examen").item(0)
              .getTextContent()), courseMap.get(id));

      /** we put the map object into the map */
      map.put(id, m);
    }

    return map;
  }

  /**
   * @param root
   *          the root of the document
   * @param comment
   *          if we need the comment attribute of the student node
   * @param markMap
   *          a map with all the marks
   * @return a map with all the students
   */
  protected Map<Integer, Student> getStudentNodes(Element root,
      boolean comment, Map<Integer, Mark> markMap) {
    final HashMap<Integer, Student> map = new HashMap<Integer, Student>();
    final NodeList nodes = root.getElementsByTagName("student");
    Student s;
    Node n;
    Element e;
    int id;

    /** for each course node */
    for (int i = 0; i < nodes.getLength(); i++) {
      /** we take a node */
      n = nodes.item(i);

      /** we create an element by using the course node */
      e = (Element) n;

      /** we get the attribute id_exmane from the element examen */
      id = new Integer(e.getAttribute("id_student"));

      /** if we want the comment attribute */
      if (comment) {
        /** we create the student object */
        s = new Student(id, e.getElementsByTagName("name").item(0)
            .getTextContent(), e.getElementsByTagName("last_name").item(0)
            .getTextContent(), e.getElementsByTagName("comment").item(0)
            .getTextContent());
      } else {
        s = new Student(id, e.getElementsByTagName("name").item(0)
            .getTextContent(), e.getElementsByTagName("last_name").item(0)
            .getTextContent());
      }

      /** we get all the student marks for this student */
      Map<Integer, StudentMark> studentMarkMap = getStudentMarkNodes(e, s,
          markMap);

      /** ************************************************************************** */
      for (StudentMark sm : studentMarkMap.values()) {
        System.out.println(sm.getValue() + " " + sm.getCoeff());
      }
      /** ************************************************************************** */

      /** we put the map object into the map */
      map.put(id, s);
    }

    return map;
  }

  /**
   * @param root
   *          the root of the document
   * @param s
   *          the student object
   * @param markMap
   *          a map with all the marks
   * @return a map with all the student marks
   */
  protected Map<Integer, StudentMark> getStudentMarkNodes(Element root,
      Student s, Map<Integer, Mark> markMap) {
    final HashMap<Integer, StudentMark> map = new HashMap<Integer, StudentMark>();
    final NodeList nodes = root.getElementsByTagName("student_mark");
    StudentMark sm;
    Node n;
    Element e;
    int courseId;
    int examenId;

    /** for each student mark node */
    for (int i = 0; i < nodes.getLength(); i++) {
      /** we take a node */
      n = nodes.item(i);

      /** we create an element by using the course node */
      e = (Element) n;

      /** we get the attributes courseId and id_examen from the element examen */
      courseId = new Integer(e.getAttribute("id_course"));
      examenId = new Integer(e.getAttribute("id_examen"));

      /** we create the mark object */
      sm = new StudentMark(s, markMap.get(examenId), Float.parseFloat(e
          .getElementsByTagName("mark").item(0).getTextContent()));

      /** we put the map object into the map */
      map.put(examenId, sm);
    }

    return map;
  }

  /**
   * create a new document object
   * 
   * @return a new document object
   */
  protected Document newDocument() {
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      /**
       * the parser produced by this code will validate documents as they are
       * parsed
       */
      dbf.setValidating(true);

      return dbf.newDocumentBuilder().parse(new File(documentName));
    } catch (ParserConfigurationException e) {
      System.out
          .println("Error during the creation of the document object : \n" + e
              + "\n");
      e.printStackTrace();
    } catch (SAXException e) {
      System.out
          .println("Error during the creation of the document object : \n" + e
              + "\n");
      e.printStackTrace();
    } catch (IOException e) {
      System.out
          .println("Error during the creation of the document object : \n" + e
              + "\n");
      e.printStackTrace();
    }

    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.umlv.symphonie.util.dataimport.DataImporter#importStudentView(java.lang.String)
   */
  public void importStudentView(String documentName) {
    this.documentName = documentName;
    HashMap<Course, Map<Integer, StudentMark>> studentViewMap;
    Document d = newDocument();
    Element root = d.getDocumentElement();

    if (!root.getAttribute("view").equals("student")) {
      throw new DOMException(DOMException.NOT_FOUND_ERR,
          "the file isn't a student view.\n");
    }

    Map<Integer, Course> courseMap = getCourseNodes(root);
    Map<Integer, Mark> markMap = getMarkNodes(root, courseMap);
    Map<Integer, Student> studentMap = getStudentNodes(root, false, markMap);
    // Map<Integer, StudentMark> studentMarkMap = getStudentMarkNodes(root,
    // studentMap, markMap);

    /** ************************************************************************** */
    for (Course c : courseMap.values()) {
      System.out.println(c.getId() + " " + c.getTitle() + " " + c.getCoeff());
    }

    for (Mark m : markMap.values()) {
      System.out.println(m.getId() + " " + m.getDesc() + " " + m.getCoeff());
    }

    for (Student s : studentMap.values()) {
      System.out.println(s.getId() + " " + s.getName() + " " + s.getLastName());
    }
    /** ************************************************************************** */
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.umlv.symphonie.util.dataimport.DataImporter#importTeacherView(java.lang.String)
   */
  public void importTeacherView(String documentName) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see fr.umlv.symphonie.util.dataimport.DataImporter#importJuryView(java.lang.String)
   */
  public void importJuryView(String documentName) {
  }
}
