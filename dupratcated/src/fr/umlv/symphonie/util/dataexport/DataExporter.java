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
	 * export a student view of the student s, using the DataManager dm for the
	 * content of the datas, and creates a file with the name documentName.
	 * 
	 * @param documentName
	 *            the name of the document exported.
	 * @param dm
	 *            the DataManager from which we will get the data.
	 * @param s
	 *            the student object
	 * @throws DataExporterException
	 *             if the xml file doesn't respect the dtd, or if there is a
	 *             problem with the bdd.
	 */
	public void exportStudentView(String documentName, DataManager dm, Student s)
			throws DataExporterException;

	/**
	 * export a teacher view of the course c, using the DataManager dm for the
	 * content of the datas, and creates a file with the name documentName.
	 * 
	 * @param documentName
	 *            the name of the document exported.
	 * @param dm
	 *            the DataManager from which we will get the data.
	 * @param c
	 *            the course object
	 * @throws DataExporterException
	 *             if the xml file doesn't respect the dtd, or if there is a
	 *             problem with the bdd.
	 */
	public void exportTeacherView(String documentName, DataManager dm, Course c)
			throws DataExporterException;

	/**
	 * export a jury view, using the DataManager dm for the content of the datas,
	 * and creates a file with the name documentName.
	 * 
	 * @param documentName
	 *            the name of the document exported.
	 * @param dm
	 *            the DataManager from which we will get the data.
	 * @throws DataExporterException
	 *             if the xml file doesn't respect the dtd, or if there is a
	 *             problem with the bdd.
	 */
	public void exportJuryView(String documentName, DataManager dm)
			throws DataExporterException;

}
