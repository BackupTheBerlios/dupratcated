/*
 * This file is part of Symphonie Created : 9 mars 2005 14:00:00
 */
package fr.umlv.symphonie.util.dataimport.xml;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.umlv.symphonie.data.Course;
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
	public void importStudentView(String documentName)
			throws DataImporterException {
		this.documentName = documentName;
		HashMap<Course, Map<Integer, StudentMark>> studentViewMap;
		Document d = newDocument();
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
	public void importTeacherView(String documentName)
			throws DataImporterException {
		this.documentName = documentName;
		Document d = newDocument();
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
	public void importJuryView(String documentName)
			throws DataImporterException {
		Document d = newDocument();
		Element root = d.getDocumentElement();

		if (!root.getAttribute("view").equals("jury")) {
			throw new DataImporterException("the file isn't a jury view.\n");
		}
	}
}
