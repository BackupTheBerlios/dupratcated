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

		final Map<Student, Map<Integer, StudentMark>> map = getStudentNodes(
				root, true, null);

		/**
		 * we update the student data : comment for (Student s : map.keySet()) {
		 * try { dm.changeStudentComment(s, s.getComment()); } catch
		 * (DataManagerException e) { throw new DataImporterException( "Error
		 * during the importation with the bdd.\n", e); } }
		 */
	}
}
