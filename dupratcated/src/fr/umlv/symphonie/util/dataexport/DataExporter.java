/*
 * This file is part of Symphonie Created : 6 mars 2005 15:07:42
 */

package fr.umlv.symphonie.util.dataexport;

import fr.umlv.symphonie.data.Course;
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
	 * @param s
	 *            the student object
	 */
	public void exportStudentView(String documentName, Student s);

	/**
	 * export a teacher view
	 * 
	 * @param documentName
	 *            the name of the document exporter
	 * @param c
	 *            the course object
	 */
	public void exportTeacherView(String documentName, Course c);

	/**
	 * export a jury view
	 * 
	 * @param documentName
	 *            the name of the document exporter
	 */
	public void exportJuryView(String documentName);

}
