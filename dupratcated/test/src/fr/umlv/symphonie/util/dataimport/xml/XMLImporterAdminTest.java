/*
 * This file is part of Symphonie Created : 9 mars 2005 18:13:54
 */
package fr.umlv.symphonie.util.dataimport.xml;

import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.util.dataimport.DataImporter;
import fr.umlv.symphonie.util.dataimport.DataImporterException;
import junit.framework.TestCase;

/**
 * @author Laurent GARCIA
 */
public class XMLImporterAdminTest extends TestCase {

	final DataImporter di = new XMLImporterAdmin();

	final DataManager dm = SQLDataManager.getInstance();

	/**
	 * A SUPRIMER c'est juste pour tester pour nous. Utiliser la bdd de fabien,
	 * les fichiers exportés seront à la racine par défaut.
	 */
	public void testImportStudentView() {
		try {
			di.importStudentView("student_view.xml", dm);
		} catch (DataImporterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A SUPRIMER c'est juste pour tester pour nous. Utiliser la bdd de fabien,
	 * les fichiers exportés seront à la racine par défaut.
	 */
	public void testImportTeacherView() {
		try {
			di.importTeacherView("teacher_view.xml", dm);
		} catch (DataImporterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A SUPRIMER c'est juste pour tester pour nous. Utiliser la bdd de fabien,
	 * les fichiers exportés seront à la racine par défaut.
	 */
	public void testImportJuryView() {
		try {
			di.importJuryView("jury_view.xml", dm);
		} catch (DataImporterException e) {
			e.printStackTrace();
		}
	}

}
