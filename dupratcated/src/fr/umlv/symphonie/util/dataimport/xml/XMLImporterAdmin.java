/*
 * This file is part of Symphonie Created : 9 mars 2005 14:00:00
 */
package fr.umlv.symphonie.util.dataimport.xml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.Mark;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.data.StudentMark;
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
					dm.changeCourseCoeff(c, c.getCoeff());
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
					dm.changeMarkCoeff(m, m.getCoeff());
					dm.changeMarkDescription(m, m.getDesc());
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
	 * @see fr.umlv.symphonie.util.dataimport.DataImporter#importTeacherView(java.lang.String)
	 */
	public void importTeacherView(String documentName, DataManager dm)
			throws DataImporterException {
		Document d = newDocument(documentName);
		Element root = d.getDocumentElement();

		if (!root.getAttribute("view").equals("teacher")) {
			throw new DataImporterException("the file isn't a teacher view.\n");
		}

		final Map<Integer, Mark> markMap = getMarkNodes(root,
				getCourseNodes(root));
		final Map<Student, Map<Integer, StudentMark>> studentAndStudentMakMap = getStudentNodes(
				root, false, markMap);

		/** we add and update all the mark data */
		for (Mark m : markMap.values()) {
			try {
				/** if we need to add */
				if (m.getId() == -1) {
					dm.addMark(m.getDesc(), m.getCoeff(), m.getCourse());
				} else {
					/** else we update */
					dm.changeMarkCoeff(m, m.getCoeff());
					dm.changeMarkDescription(m, m.getDesc());
				}
			} catch (DataManagerException e) {
				throw new DataImporterException(
						"Error during the importation with the bdd.\n", e);
			}
		}
		
		/** we update the student data : comment */
		for (Student s : studentAndStudentMakMap.keySet()) {
			try {
				/** if we need to add */
				if (s.getId() == -1) {
					dm.addStudent(s.getName(), s.getLastName());
				} else {
					/** else we update */
					dm.changeStudentName(s, s.getName());
					dm.changeStudentLastName(s, s.getLastName());
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

		/** we add and update all the course data */
		for (Course c : courseMap.values()) {
			try {
				/** if we need to add */
				if (c.getId() == -1) {
					dm.addCourse(c.getTitle(), c.getCoeff());
				} else {
					/** else we update */
					dm.changeCourseTitle(c, c.getTitle());
					dm.changeCourseCoeff(c, c.getCoeff());
				}
			} catch (DataManagerException e) {
				throw new DataImporterException(
						"Error during the importation with the bdd.\n", e);
			}
		}
		
		/** we update the student data : comment */
		for (Student s : studentAndStudentMakMap.keySet()) {
			try {
				/** if we need to add */
				if (s.getId() == -1) {
					dm.addStudent(s.getName(), s.getLastName());
					dm.changeStudentComment(s, s.getComment());
				} else {
					/** else we update */
					dm.changeStudentComment(s, s.getComment());
					dm.changeStudentName(s, s.getName());
					dm.changeStudentLastName(s, s.getLastName());
				}				
			} catch (DataManagerException e) {
				throw new DataImporterException(
						"Error during the importation with the bdd.\n", e);
			}
		}
	}
}
