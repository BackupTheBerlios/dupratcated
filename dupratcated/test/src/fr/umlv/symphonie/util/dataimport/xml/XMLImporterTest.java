/*
 * This file is part of Symphonie Created : 8 mars 2005 21:52:16
 */

package fr.umlv.symphonie.util.dataimport.xml;

import junit.framework.TestCase;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.util.dataimport.DataImporter;
import fr.umlv.symphonie.util.dataimport.DataImporterException;
import fr.umlv.symphonie.util.dataimport.xml.XMLImporter;

/**
 * @author Laurent GARCIA
 */
public class XMLImporterTest extends TestCase {

	final DataImporter di = new XMLImporter();

	final DataManager dm = new SQLDataManager();

	public void testImportStudentView() {
		try {
			di.importStudentView("student_view.xml", dm);
		} catch (DataImporterException e) {
			;
		}
	}

	public void testImportTeacherView() {
		try {
			di.importTeacherView("teacher_view.xml", dm);
		} catch (DataImporterException e) {
			e.printStackTrace();
		}
	}

	public void testImportJuryView() {
		try {
			di.importJuryView("jury_view.xml", dm);
		} catch (DataImporterException e) {
			e.printStackTrace();
		}
	}

}
