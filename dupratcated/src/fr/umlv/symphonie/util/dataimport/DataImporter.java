 /*
 * This file is part of Symphonie Created : 8 mars 2005 21:37:19
 */

package fr.umlv.symphonie.util.dataimport;

/**
 * @author Laurent GARCIA
 */
public interface DataImporter {

  /**
   * import a student view
   * 
   * @param documentName
   *          the name of the document
   * @throws DataImporterException
   */
  public void importStudentView(String documentName)
      throws DataImporterException;

  /**
   * import a teacher view
   * 
   * @param documentName
   *          the name of the document
   * @throws DataImporterException
   */
  public void importTeacherView(String documentName)
      throws DataImporterException;

  /**
   * import a jury view
   * 
   * @param documentName
   *          the name of the document
   * @throws DataImporterException
   */
  public void importJuryView(String documentName) throws DataImporterException;
}
