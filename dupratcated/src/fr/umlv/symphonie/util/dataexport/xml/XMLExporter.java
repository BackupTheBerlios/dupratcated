/*
 * This file is part of Symphonie Created : 1 mars 2005 16:09:01
 */

package fr.umlv.symphonie.util.export.xml;

import java.io.File;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.util.export.DataExporter;

/**
 * @author Laurent GARCIA
 */
public abstract class XMLExporter implements DataExporter {

  protected String documentName;
  protected final File dtd = new File("src/fr/umlv/symphonie/util/export/xml/symphonie.dtd");
  protected SQLDataManager dm = new SQLDataManager();

  /**
   * add a course node
   * 
   * @param root
   *          the parent node of the course node
   * @param before
   *          we put the course node before this node, if null we put the node
   *          at the end of the root node
   * @param c
   *          the course object
   */
  protected static void addCourseNode(Node root, Node before, Course c) {
    Node course;

    /** <course id_course="?">... </course> */
    Element e = root.getOwnerDocument().createElement("course");
    e.setAttribute("id_course", "" + c.getId());

    /** if we have to put the new node at the end */
    if (before == null) {
      course = root.appendChild(e);
    } else {
      course = root.insertBefore(e, before);
    }

    /** <title>? </title> */
    e = course.getOwnerDocument().createElement("title");
    Node n = course.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode(c.getTitle()));

    /** <coeff_course>? </coeff_course> */
    e = course.getOwnerDocument().createElement("coeff_course");
    n = course.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode("" + c.getCoeff()));
  }

  /**
   * add an examen node
   * 
   * @param root
   *          the parent node of the student mark node
   * @param sm
   *          the student mark object
   */
  protected static void addExamenNode(Node root, StudentMark sm) {
    Node examen;

    /** <examen id_examen="?">... </examen> */
    Element e = root.getOwnerDocument().createElement("examen");
    e.setAttribute("id_examen", "" + sm.getMark().getId());
    examen = root.appendChild(e);

    /** <desc>? </desc> */
    e = examen.getOwnerDocument().createElement("desc");
    Node n = examen.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode(sm.getMark().getDesc()));

    /** <coeff_examen>? </coeff_examen> */
    e = examen.getOwnerDocument().createElement("coeff_examen");
    n = examen.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode("" + sm.getCoeff()));
  }

  /**
   * add a student node
   * 
   * @param root
   *          the parent node of the student node
   * @param s
   *          the student object
   * @return the new student node
   */
  protected static Node addStudentNode(Node root, Student s) {
    Node student;

    /** <student id_student="?">... </student> */
    Element e = root.getOwnerDocument().createElement("student");
    e.setAttribute("id_student", "" + s.getId());
    student = root.appendChild(e);

    /** <name>? </name> */
    e = student.getOwnerDocument().createElement("name");
    Node n = student.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode(s.getName()));

    /** <last_name>? </last_name> */
    e = student.getOwnerDocument().createElement("last_name");
    n = student.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode(s.getLastName()));

    /** <comment>? </comment> */
    e = student.getOwnerDocument().createElement("comment");
    n = student.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode(s.getComment()));

    return student;
  }

  /**
   * add a mark node
   * 
   * @param root
   *          the parent node the of student mark node
   * @param sm
   *          the student mark object
   */
  protected static void addMarkNode(Node root, StudentMark sm) {
    Node mark;

    /** <student_mark id_course="?" id_examen=?">... </student_mark> */
    Element e = root.getOwnerDocument().createElement("student_mark");
    e.setAttribute("id_course", "" + sm.getCourse().getId());
    e.setAttribute("id_examen", "" + sm.getMark().getId());
    mark = root.appendChild(e);

    /** <mark>? </mark> */
    e = mark.getOwnerDocument().createElement("mark");
    Node n = mark.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode("" + sm.getValue()));
  }

  /**
   * create a new document object
   * 
   * @return a new document object
   */
  protected Document newDocument() {
    try {
      /** we create a new document object */
      return DocumentBuilderFactory.newInstance().newDocumentBuilder()
          .newDocument();
    } catch (ParserConfigurationException e) {
      System.out
          .println("Error during the creation of the document object : \n" + e
              + "\n");
      e.printStackTrace();
    }

    return null;
  }

  /**
   * write the document
   * 
   * @param document
   *          the document object
   */
  protected void writeDocument(Document document) {
    try {
      Transformer transformer = TransformerFactory.newInstance()
          .newTransformer();

      /** <!DOCTYPE symphonie SYSTEM "file:?"> */
      transformer
          .setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM, dtd
              .toURL().toString());

      /** indention of the tags */
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");

      /** <?xml version="1.0" encoding="ISO-8859-1"?> */
      transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

      /** we write the document */
      transformer.transform(new DOMSource(document), new StreamResult(new File(
          documentName)));

    } catch (TransformerConfigurationException e) {
      System.out.println("Error during the exportation : \n" + e + "\n");
      e.printStackTrace();
    } catch (TransformerException e) {
      System.out.println("Error during the exportation : \n" + e + "\n");
      e.printStackTrace();
    } catch (TransformerFactoryConfigurationError e) {
      System.out.println("Error during the exportation : \n" + e + "\n");
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      System.out.println("Error during the exportation with the dtd : \n" + e
          + "\n");
      e.printStackTrace();
    } catch (MalformedURLException e) {
      System.out.println("Error during the exportation with the dtd : \n" + e
          + "\n");
      e.printStackTrace();
    }
  }

}
