/*
 * This file is part of Symphonie Created : 6 mars 2005 15:07:42
 */

package fr.umlv.symphonie.util.dataexport;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.Student;

/**
 * @author Laurent GARCIA
 */
public interface DataExporter {

	/**
	 * export a student view
	 * 
	 * @param documentName
	 *            the name of the document exporter
	 * @param dm
	 *            the data
	 * @param s
	 *            the student object
	 * @throws DataExporterException
	 */
	public void exportStudentView(String documentName, DataManager dm, Student s)
			throws DataExporterException;

	/**
	 * export a teacher view
	 * 
	 * @param documentName
	 *            the name of the document exporter
	 * @param dm
	 *            the data
	 * @param c
	 *            the course object
	 * @throws DataExporterException
	 */
	public void exportTeacherView(String documentName, DataManager dm, Course c)
			throws DataExporterException;

	/**
	 * export a jury view
	 * 
	 * @param documentName
	 *            the name of the document exporter
	 * @param dm
	 *            the data
	 * @throws DataExporterException
	 */
	public void exportJuryView(String documentName, DataManager dm)
			throws DataExporterException;

}
