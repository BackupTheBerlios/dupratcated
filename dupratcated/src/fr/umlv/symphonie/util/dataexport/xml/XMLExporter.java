/*
 * This file is part of Symphonie Created : 1 mars 2005 16:09:01
 */

package fr.umlv.symphonie.util.dataexport.xml;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

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
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
import fr.umlv.symphonie.data.formula.Formula;
import fr.umlv.symphonie.util.Pair;
import fr.umlv.symphonie.util.dataexport.DataExporter;
import fr.umlv.symphonie.util.dataexport.DataExporterException;

/**
 * @author Laurent GARCIA
 */
public class XMLExporter implements DataExporter {

	/**
	 * add a course node
	 * 
	 * @param root
	 *            the parent node of the course node
	 * @param c
	 *            the course object
	 */
	private static void addCourseNode(Node root, Course c) {
		final Node course;

		/** <course id_course="?">... </course> */
		Element e = root.getOwnerDocument().createElement("course");
		e.setAttribute("id_course", "" + c.getId());
		course = root.appendChild(e);

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
	 *            the parent node of the student mark node
	 * @param sm
	 *            the student mark object
	 */
	private static void addExamenNode(Node root, StudentMark sm) {
		final Node examen;

		/** <examen id_examen="?" id_course="?">... </examen> */
		Element e = root.getOwnerDocument().createElement("examen");
		e.setAttribute("id_examen", "" + sm.getMark().getId());
		e.setAttribute("id_course", "" + sm.getCourse().getId());
		examen = root.appendChild(e);

		/** <desc>? </desc> */
		e = examen.getOwnerDocument().createElement("desc");
		Node n = examen.appendChild(e);
		n.appendChild(n.getOwnerDocument().createTextNode(
				sm.getMark().getDesc()));

		/** <coeff_examen>? </coeff_examen> */
		e = examen.getOwnerDocument().createElement("coeff_examen");
		n = examen.appendChild(e);
		n.appendChild(n.getOwnerDocument().createTextNode("" + sm.getCoeff()));
	}

	/**
	 * add a student node
	 * 
	 * @param root
	 *            the parent node of the student node
	 * @param s
	 *            the student object
	 * @param comment
	 *            the comment node
	 * @return the new student node
	 */
	private static Node addStudentNode(Node root, Student s, boolean comment) {
		final Node student;

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

		if (comment) {
			/** <comment>? </comment> */
			e = student.getOwnerDocument().createElement("comment");
			n = student.appendChild(e);
			n.appendChild(n.getOwnerDocument().createTextNode(s.getComment()));
		}
		return student;
	}

	/**
	 * add a mark node
	 * 
	 * @param root
	 *            the parent node of the student mark node
	 * @param sm
	 *            the student mark object
	 */
	private static void addMarkNode(Node root, StudentMark sm) {
		final Node mark;

		/** <student_mark id_examen=?">... </student_mark> */
		Element e = root.getOwnerDocument().createElement("student_mark");
		e.setAttribute("id_examen", "" + sm.getMark().getId());
		mark = root.appendChild(e);

		/** <mark>? </mark> */
		e = mark.getOwnerDocument().createElement("mark");
		Node n = mark.appendChild(e);
		n.appendChild(n.getOwnerDocument().createTextNode("" + sm.getValue()));
	}

	/**
	 * add a formula node
	 * 
	 * @param root
	 *            the parent node of the formula node
	 * @param f
	 *            the formula object
	 */
	private static void addFormulaNode(Node root, Formula f) {
		final Node formula;

		/** <formula id_formula="?">... </formula> */
		Element e = root.getOwnerDocument().createElement("formula");
		e.setAttribute("id_formula", "" + f.getID());
		formula = root.appendChild(e);

		/** <column>? </column> */
		e = formula.getOwnerDocument().createElement("column");
		Node n = formula.appendChild(e);
		n.appendChild(n.getOwnerDocument().createTextNode("" + f.getColumn()));

		/** <f_name>? </f_name> */
		e = formula.getOwnerDocument().createElement("f_name");
		n = formula.appendChild(e);
		n.appendChild(n.getOwnerDocument().createTextNode(
				"" + f.getDescription()));

		/** <expression>? </expression> */
		e = formula.getOwnerDocument().createElement("expression");
		n = formula.appendChild(e);
		n.appendChild(n.getOwnerDocument().createTextNode(f.toString()));
	}

	/**
	 * create a new document object
	 * 
	 * @return a new document object
	 * @throws DataExporterException
	 *             if a DocumentBuilder cannot be created which satisfies the
	 *             configuration requested
	 */
	private Document newDocument() throws DataExporterException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

			/**
			 * the parser produced by this code will validate documents as they
			 * are parsed
			 */
			dbf.setValidating(true);

			return dbf.newDocumentBuilder().newDocument();
		} catch (ParserConfigurationException e) {
			throw new DataExporterException(
					"Error during the creation of the document object : \n", e);
		}

	}

	/**
	 * write the document
	 * 
	 * @param document
	 *            the document object
	 * @param documentName
	 *            the name of the document
	 * @throws DataExporterException
	 *             if it is not possible to create a Transformer instance, or if
	 *             an unrecoverable error occurs during the course of the
	 *             transformation, or if the documentName cannot be parsed as a
	 *             URL.
	 */
	private void writeDocument(Document document, String documentName)
			throws DataExporterException {
		try {
			final Transformer transformer = TransformerFactory.newInstance()
					.newTransformer();

			/** <!DOCTYPE symphonie SYSTEM "file:?"> */
			transformer
					.setOutputProperty(
							javax.xml.transform.OutputKeys.DOCTYPE_SYSTEM,
							new File(
									"src/fr/umlv/symphonie/util/dataexport/xml/symphonie.dtd")
									.toURL().toString());

			/** indention of the tags */
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			/** <?xml version="1.0" encoding="ISO-8859-1"?> */
			transformer.setOutputProperty(OutputKeys.ENCODING, "ISO-8859-1");

			/** we write the document */
			transformer.transform(new DOMSource(document), new StreamResult(
					new File(documentName)));

		} catch (TransformerConfigurationException e) {
			throw new DataExporterException(
					"Error during the exportation : \n", e);
		} catch (TransformerException e) {
			throw new DataExporterException(
					"Error during the exportation : \n", e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new DataExporterException(
					"Error during the exportation : \n", e);
		} catch (IllegalArgumentException e) {
			throw new DataExporterException(
					"Error during the exportation : \n", e);
		} catch (MalformedURLException e) {
			throw new DataExporterException(
					"Error during the exportation : \n", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.util.dataexport.DataExporter#exportStudentView(java.lang.String,
	 *      fr.umlv.symphonie.data.Student)
	 */
	public void exportStudentView(String documentName, DataManager dm, Student s)
			throws DataExporterException {
		final Document document = newDocument();
		HashMap<Course, Map<Integer, StudentMark>> map = null;

		try {
			map = (HashMap<Course, Map<Integer, StudentMark>>) dm
					.getAllMarksByStudent(s);
		} catch (DataManagerException e) {
			throw new DataExporterException(
					"Error exporting the student view with the bdd\n", e);
		}

		/** <symphonie view="student">... </symphonie> */
		final Element e = document.createElement("symphonie");
		e.setAttribute("view", "student");
		final Node root = document.appendChild(e);

		/** we export all the courses */
		for (Course c : map.keySet()) {
			addCourseNode(root, c);
		}

		/** we export the student of the student view */
		final Node studentNode = addStudentNode(root, s, false);

		/**
		 * we export all the marks for the student of the student view and all
		 * the examens
		 */
		for (Map<Integer, StudentMark> tmp : map.values()) {
			for (StudentMark sm : tmp.values()) {
				addExamenNode(root, sm);
				addMarkNode(studentNode, sm);
			}
		}

		writeDocument(document, documentName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.util.dataexport.DataExporter#exportTeacherView(java.lang.String,
	 *      fr.umlv.symphonie.data.Course)
	 */
	public void exportTeacherView(String documentName, DataManager dm, Course c)
			throws DataExporterException {
		final Document document = newDocument();
		Pair<Map<Integer, Mark>, SortedMap<Student, Map<Integer, StudentMark>>> pair = null;
		ArrayList<Formula> list = null;
		Map<Integer, StudentMark> map;
		Node studentNode;
		final int idLastStudent;
		int i = 1;

		try {
			pair = dm.getAllMarksByCourse(c);
			list = (ArrayList<Formula>) dm.getFormulasByCourse(c);
		} catch (DataManagerException e) {
			throw new DataExporterException(
					"Error exporting the teacher view with the bdd\n", e);
		}

		final SortedMap<Student, Map<Integer, StudentMark>> sortedMap = pair
				.getSecond();

		/** <symphonie view="teacher">... </symphonie> */
		final Element e = document.createElement("symphonie");
		e.setAttribute("view", "teacher");
		final Node root = document.appendChild(e);

		/** we export the course of the teacher view */
		addCourseNode(root, c);

		idLastStudent = sortedMap.size();

		/** we export all the students */
		for (Student s : sortedMap.keySet()) {
			studentNode = addStudentNode(root, s, false);
			map = sortedMap.get(s);

			/** we export all the marks for one student and all the examens */
			for (StudentMark sm : map.values()) {

				/**
				 * if it the last student node, we can create the examens nodes
				 * since it has to be at the end of the xml
				 */
				if (idLastStudent == i) {
					addExamenNode(root, sm);
				}

				addMarkNode(studentNode, sm);
			}
			i++;
		}

		if (list != null) {
			/** we export all the formulas for this course */
			for (Formula f : list) {
				addFormulaNode(root, f);
			}
		}

		/** we create the document */
		writeDocument(document, documentName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.umlv.symphonie.util.dataexport.DataExporter#exportJuryView(java.lang.String)
	 */
	public void exportJuryView(String documentName, DataManager dm)
			throws DataExporterException {
		final Document document = newDocument();
		Pair<Map<Integer, Course>, SortedMap<Student, Map<Course, Map<Integer, StudentMark>>>> pair = null;
		ArrayList<Formula> list = null;
		Node studentNode;

		try {
			pair = dm.getAllStudentsMarks();
			list = (ArrayList<Formula>) dm.getJuryFormulas();
		} catch (DataManagerException e) {
			throw new DataExporterException(
					"Error exporting the student view with the bdd\n", e);
		}

		final HashMap<Integer, Course> map = (HashMap<Integer, Course>) pair
				.getFirst();
		final SortedMap<Student, Map<Course, Map<Integer, StudentMark>>> sortedMap = pair
				.getSecond();

		/** <symphonie view="jury">... </symphonie> */
		final Element e = document.createElement("symphonie");
		e.setAttribute("view", "jury");
		final Node root = document.appendChild(e);

		/** for each course */
		for (Course c : map.values()) {
			addCourseNode(root, c);
		}

		/** we export all the students */
		for (Student s : sortedMap.keySet()) {
			studentNode = addStudentNode(root, s, true);
		}

		if (list != null) {
			/** we export all the formulas */
			for (Formula f : list) {
				addFormulaNode(root, f);
			}
		}
		
		writeDocument(document, documentName);
	}

}
