 /*
 * This file is part of Symphonie Created : 9 mars 2005 18:35:29
 */

package fr.umlv.symphonie.util.dataimport;

/**
 * @author Laurent GARCIA
 */
public class DataImporterException extends Exception {

  /**
   * @param message
   *          the message
   */
  public DataImporterException(String message) {
    super(message);
  }

  /**
   * @param cause
   *          the cause
   */
  public DataImporterException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public DataImporterException(String message, Throwable cause) {
    super(message, cause);
  }
}
