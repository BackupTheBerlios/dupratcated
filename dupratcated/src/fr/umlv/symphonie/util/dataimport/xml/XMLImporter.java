/*
 * This file is part of Symphonie Created : 8 mars 2005 21:36:42
 */

package fr.umlv.symphonie.util.dataimport.xml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.util.dataimport.DataImporter;
import fr.umlv.symphonie.util.dataimport.DataImporterException;

/**
 * @author Laurent GARCIA
 */
public class XMLImporter implements DataImporter {

	protected String documentName;

	protected final DataManager dm = new SQLDataManager();

	/**
	 * @param root
	 *            the root of the document
	 * @param coeff
	 *            if true we create the id_coeff attribute
	 * @return a map with all the courses
	 */
	protected Map<Integer, Course> getCourseNodes(Element root, boolean coeff) {
		final HashMap<Integer, Course> map = new HashMap<Integer, Course>();
		final NodeList nodes = root.getElementsByTagName("course");
		Course c;
		Node n;
		Element e;
		int id;
		float coeffCourse = -1;

		/** for each course node */
		for (int i = 0; i < nodes.getLength(); i++) {
			/** we take a node */
			n = nodes.item(i);

			/** we create an element by using the course node */
			e = (Element) n;

			id = Integer.parseInt(e.getAttribute("id_course"));

			/** if we need the coeff_course attribute */
			if (coeff) {
				/** we get the attribute id_course from the element course */
				coeffCourse = Float.parseFloat(e.getElementsByTagName(
						"coeff_course").item(0).getTextContent());
			}
			/** we create the course object */
			c = new Course(id, e.getElementsByTagName("title").item(0)
					.getTextContent(), coeffCourse);

			/** we put the course object into the map */
			map.put(id, c);
		}

		return map;
	}

	/**
	 * @param root
	 *            the root of the document
	 * @param courseMap
	 *            a map with all the courses
	 * @return a map with all the marks
	 */
	protected Map<Integer, Mark> getMarkNodes(Element root,
			Map<Integer, Course> courseMap) {
		final HashMap<Integer, Mark> map = new HashMap<Integer, Mark>();
		final NodeList nodes = root.getElementsByTagName("examen");
		Mark m;
		Node n;
		Element e;
		int examenId;
		int courseId;

		/** for each course node */
		for (int i = 0; i < nodes.getLength(); i++) {
			/** we take a node */
			n = nodes.item(i);

			/** we create an element by using the course node */
			e = (Element) n;

			/**
			 * we get the attributes id_examen and id_course from the element
			 * examen
			 */
			examenId = Integer.parseInt(e.getAttribute("id_examen"));
			courseId = Integer.parseInt(e.getAttribute("id_course"));

			/** we create the mark object */
			m = new Mark(examenId, e.getElementsByTagName("desc").item(0)
					.getTextContent(), Float.parseFloat(e.getElementsByTagName(
					"coeff_examen").item(0).getTextContent()), courseMap
					.get(courseId));

			/** we put the map object into the map */
			map.put(examenId, m);
		}

		return map;
	}

	/**
	 * @param root
	 *            the root of the document
	 * @param comment
	 *            if true we create the comment node
	 * @param markMap
	 *            a map with all the marks
	 * @return a map with all the students
	 */
	protected Map<Student, Map<Integer, StudentMark>> getStudentNodes(
			Element root, boolean comment, Map<Integer, Mark> markMap) {
		final Map<Student, Map<Integer, StudentMark>> map = new HashMap<Student, Map<Integer, StudentMark>>();
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
			id = Integer.parseInt(e.getAttribute("id_student"));

			/** if we want the comment attribute */
			if (comment) {
				/** we create the student object */
				s = new Student(id, e.getElementsByTagName("name").item(0)
						.getTextContent(), e.getElementsByTagName("last_name")
						.item(0).getTextContent(), e.getElementsByTagName(
						"comment").item(0).getTextContent());
			} else {
				s = new Student(id, e.getElementsByTagName("name").item(0)
						.getTextContent(), e.getElementsByTagName("last_name")
						.item(0).getTextContent());
			}

			/**
			 * if we need the student arks, we get and put them in the map
			 */
			if (markMap != null) {
				map.put(s, getStudentMarkNodes(e, s, markMap));
			} else {
				map.put(s, null);
			}

		}

		return map;
	}

	/**
	 * @param root
	 *            the root of the document
	 * @param s
	 *            the student object
	 * @param markMap
	 *            a map with all the marks
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

			/**
			 * we get the attributes courseId and id_examen from the element
			 * examen
			 */
			courseId = Integer.parseInt(e.getAttribute("id_course"));
			examenId = Integer.parseInt(e.getAttribute("id_examen"));

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
	 * @throws DataImporterException
	 */
	protected Document newDocument() throws DataImporterException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			/**
			 * the parser produced by this code will validate documents as they
			 * are parsed
			 */
			dbf.setValidating(true);

			return dbf.newDocumentBuilder().parse(new File(documentName));
		} catch (ParserConfigurationException e) {
			throw new DataImporterException(
					"Error during the creation of the document object : \n", e);
		} catch (SAXException e) {
			throw new DataImporterException(
					"Error during the creation of the document object : \n", e);
		} catch (IOException e) {
			throw new DataImporterException(
					"Error during the creation of the document object : \n", e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.util.dataimport.DataImporter#importStudentView(java.lang.String)
	 */
	public void importStudentView(String documentName)
			throws DataImporterException {

		throw new DataImporterException(
				"only a admin can import a student view.\n");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.util.dataimport.DataImporter#importTeacherView(java.lang.String)
	 */
	public void importTeacherView(String documentName)
			throws DataImporterException {
		this.documentName = documentName;
		Document d = newDocument();
		Element root = d.getDocumentElement();

		if (!root.getAttribute("view").equals("teacher")) {
			throw new DataImporterException("the file isn't a teacher view.\n");
		}

		final Map<Integer, Mark> markMap = getMarkNodes(root, getCourseNodes(
				root, false));
		final Map<Student, Map<Integer, StudentMark>> studentAndStudentMakMap = getStudentNodes(
				root, false, markMap);

		/** we update the mark data : desc et coeff */
		for (Mark m : markMap.values()) {
			try {
				dm.changeMarkCoeff(m, m.getCoeff());
				dm.changeMarkDescription(m, m.getDesc());
			} catch (DataManagerException e) {
				throw new DataImporterException(
						"Error during the importation with the bdd.\n", e);
			}
		}

		/** we update the student mark data : mark */
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
	 * @see fr.umlv.symphonie.util.dataimport.DataImporter#importJuryView(java.lang.String)
	 */
	public void importJuryView(String documentName)
			throws DataImporterException {
		this.documentName = documentName;
		Document d = newDocument();
		Element root = d.getDocumentElement();

		if (!root.getAttribute("view").equals("jury")) {
			throw new DataImporterException("the file isn't a jury view.\n");
		}

		final Map<Student, Map<Integer, StudentMark>> map = getStudentNodes(
				root, true, null);

		/** we update the student data : comment */
		for (Student s : map.keySet()) {
			try {
				dm.changeStudentComment(s, s.getComment());
			} catch (DataManagerException e) {
				throw new DataImporterException(
						"Error during the importation with the bdd.\n", e);
			}
		}
	}
}
