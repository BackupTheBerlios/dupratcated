/*
 * This file is part of Symphonie Created : 8 mars 2005 21:37:19
 */

package fr.umlv.symphonie.util.dataimport;

import fr.umlv.symphonie.data.DataManager;

/**
 * @author Laurent GARCIA
 */
public interface DataImporter {

	/**
	 * import a student view with the datas which are in the file documentName,
	 * and set or create the data in the bdd with the DataManager dm. If an id
	 * of a node has the value -1, the datas will be created, else they will be
	 * updated.
	 * 
	 * @param documentName
	 *            the name of the document imported.
	 * @param dm
	 *            the DataManager with which we will set or create the data.
	 * @throws DataImporterException
	 *             if the xml file doesn't respect the dtd, or if there is a
	 *             problem with the bdd, or if the xml file doesn't exist, or if
	 *             the xml file isn't in the correct view.
	 */
	public void importStudentView(String documentName, DataManager dm)
			throws DataImporterException;

	/**
	 * import a teacher view with the datas which are in the file documentName,
	 * and set or create the data in the bdd with the DataManager dm. If an id
	 * of a node has the value -1, the datas will be created, else they will be
	 * updated.
	 * 
	 * @param documentName
	 *            the name of the document imported.
	 * @param dm
	 *            the DataManager with which we will set or create the data.
	 * @throws DataImporterException
	 *             if the xml file doesn't respect the dtd, or if there is a
	 *             problem with the bdd, or if the xml file doesn't exist, or if
	 *             the xml file isn't in the correct view.
	 */
	public void importTeacherView(String documentName, DataManager dm)
			throws DataImporterException;

	/**
	 * import a jury view with the datas which are in the file documentName, and
	 * set or create the data in the bdd with the DataManager dm. If an id of a
	 * node has the value -1, the datas will be created, else they will be
	 * updated.
	 * 
	 * @param documentName
	 *            the name of the document imported.
	 * @param dm
	 *            the DataManager with which we will set or create the data.
	 * @throws DataImporterException
	 *             if the xml file doesn't respect the dtd, or if there is a
	 *             problem with the bdd, or if the xml file doesn't exist, or if
	 *             the xml file isn't in the correct view.
	 */
	public void importJuryView(String documentName, DataManager dm)
			throws DataImporterException;
}
