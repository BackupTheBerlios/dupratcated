/*
 * This file is part of Symphonie Created : 1 mars 2005 16:09:01
 */

package fr.umlv.symphonie.util.export.xml;

import java.io.File;
import java.io.IOException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;

/**
 * @author SnowMan
 */
public abstract class XMLExporter {

  protected String documentName;
  protected final String dtd = "symphonie.dtd";
  protected SQLDataManager dm = new SQLDataManager();

  public abstract void export() throws  DataManagerException;

  protected static void addCourseNode(Node root, Course c) {
    Element e = root.getOwnerDocument().createElement("course");
    e.setAttribute("id_course", "" + c.getId());
    Node n = root.appendChild(e);

    e = n.getOwnerDocument().createElement("title");
    e.setNodeValue(c.getTitle());
    n.appendChild(e);

    e = n.getOwnerDocument().createElement("coeff_course");
    e.setNodeValue("" + c.getCoeff());
    n.appendChild(e);
  }

  protected static void addExamenNode(Node root, StudentMark sm) {
    Element e = root.getOwnerDocument().createElement("examen");
    e.setAttribute("id_examen", "" + sm.getMark().getId());
    Node n = root.appendChild(e);

    e = n.getOwnerDocument().createElement("desc");
    e.setNodeValue(sm.getMark().getDesc());
    n.appendChild(e);

    e = n.getOwnerDocument().createElement("coeff_examen");
    e.setNodeValue("" + sm.getCoeff());
    n.appendChild(e);
  }

  protected static Node addStudentNode(Node root, Student s) {
    Element e = root.getOwnerDocument().createElement("student");
    e.setAttribute("id_student", "" + s.getId());
    Node n = root.appendChild(e);

    e = n.getOwnerDocument().createElement("name");
    e.setNodeValue(s.getName());
    n.appendChild(e);

    e = n.getOwnerDocument().createElement("last_name");
    e.setNodeValue(s.getLastName());
    n.appendChild(e);

    e = n.getOwnerDocument().createElement("comment");
    e.setNodeValue(s.getComment());

    return n.appendChild(e);
  }

  protected static void addMarkNode(Node root, StudentMark sm) {
    Element e = root.getOwnerDocument().createElement("student_mark");
    e.setAttribute("id_course", "" + sm.getCourse().getId());
    e.setAttribute("id_examen", "" + sm.getMark().getId());
    Node n = root.appendChild(e);

    e = n.getOwnerDocument().createElement("mark");
    e.setNodeValue("" + sm.getValue());
    n.appendChild(e);
  }
  
  protected Document newDocument(){
    try {
      return DocumentBuilderFactory.newInstance()
      .newDocumentBuilder().parse(new File(documentName));
    } catch (SAXException e) {
      System.out.println("Error during the creation of the document object : \n" + e + "\n");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("Error during the creation of the document object : \n" + e + "\n");
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      System.out.println("Error during the creation of the document object : \n" + e + "\n");
      e.printStackTrace();
    }
    
    return null;
  }
  
  protected void makeDocument(Document document){
    try {
      TransformerFactory.newInstance().newTransformer().transform(
          new DOMSource(document), new StreamResult(new File(documentName)));
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

  public boolean isValidate() {

    Document document = newDocument();

    Validator validator = null;
    
    try {
      validator = SchemaFactory.newInstance(
          XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
          new StreamSource(new File(dtd).getAbsolutePath())).newValidator();
    } catch (SAXException e) {
      System.out.println("Error during the validation : \n" + e + "\n");
      e.printStackTrace();
      return false;
    }

      try {
        validator.validate(new DOMSource(document));
      } catch (SAXException e) {
        System.out.println("Error during the validation : \n" + e + "\n");
        e.printStackTrace();
        return false;
      } catch (IOException e) {
        System.out.println("Error during the validation : \n" + e + "\n");
        e.printStackTrace();  
        return false;  
      }
    
    return true;
  }
  
}
