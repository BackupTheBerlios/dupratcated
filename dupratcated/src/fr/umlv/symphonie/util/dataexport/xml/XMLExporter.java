/*
 * This file is part of Symphonie Created : 1 mars 2005 16:09:01
 */

package fr.umlv.symphonie.util.export.xml;

import java.io.File;

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
 * @author SnowMan
 */
public abstract class XMLExporter implements DataExporter {

  protected String documentName;
  protected final String dtd = "symphonie.dtd";
  protected SQLDataManager dm = new SQLDataManager();

  protected static void addCourseNode(Node root, Course c) {
    Element e = root.getOwnerDocument().createElement("course");
    e.setAttribute("id_course", "" + c.getId());
    Node course = root.appendChild(e);

    e  = course.getOwnerDocument().createElement("title");
    Node n = course.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode(c.getTitle()));
    
    e  = course.getOwnerDocument().createElement("coeff_course");
    n = course.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode("" + c.getCoeff()));
  }

  protected static void addExamenNode(Node root, StudentMark sm) {
    Element e = root.getOwnerDocument().createElement("examen");
    e.setAttribute("id_examen", "" + sm.getMark().getId());
    Node examen = root.appendChild(e);

    e  = examen.getOwnerDocument().createElement("desc");
    Node n = examen.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode(sm.getMark().getDesc()));

    e  = examen.getOwnerDocument().createElement("coeff_examen");
    n = examen.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode("" + sm.getCoeff()));   
  }

  protected static Node addStudentNode(Node root, Student s) {
    Element e = root.getOwnerDocument().createElement("student");
    e.setAttribute("id_student", "" + s.getId());
    Node student = root.appendChild(e);

    e  = student.getOwnerDocument().createElement("name");
    Node n = student.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode(s.getName()));

    e  = student.getOwnerDocument().createElement("last_name");
    n = student.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode(s.getLastName()));

    e  = student.getOwnerDocument().createElement("comment");
    n = student.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode(s.getComment()));

    return student;
  }

  protected static void addMarkNode(Node root, StudentMark sm) {
    Element e = root.getOwnerDocument().createElement("student_mark");
    e.setAttribute("id_course", "" + sm.getCourse().getId());
    e.setAttribute("id_examen", "" + sm.getMark().getId());
    Node mark = root.appendChild(e);

    e  = mark.getOwnerDocument().createElement("mark");
    Node n = mark.appendChild(e);
    n.appendChild(n.getOwnerDocument().createTextNode("" + sm.getValue()));
  }
  
  protected Document newDocument(){
    try {
     return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException e) {
      System.out.println("Error during the creation of the document object : \n" + e + "\n");
      e.printStackTrace();
    }
    
    return null;
  }
  
  protected void makeDocument(Document document){
    try {

      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM, "file:" + dtd);
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");
      transformer.transform(new DOMSource(document), new StreamResult(new File(documentName)));
     
    } catch (TransformerConfigurationException e) {
      System.out.println("Error during the exportation : \n" + e + "\n");
      e.printStackTrace();
    } catch (TransformerException e) {
      System.out.println("Error during the exportation : \n" + e + "\n");
      e.printStackTrace();
    } catch (TransformerFactoryConfigurationError e) {
      System.out.println("Error during the exportation : \n" + e + "\n");
      e.printStackTrace();
    }
  }
  
}
