/*
 * This file is part of Symphonie Created : 9 mars 2005 18:27:33
 */

package fr.umlv.symphonie.util.dataexport;

/**
 * @author Laurent GARCIA
 */
public class DataExporterException extends Exception {

  /**
   * @param message
   *          the message
   */
  public DataExporterException(String message) {
    super(message);
  }

  /**
   * @param cause
   *          the cause
   */
  public DataExporterException(Throwable cause) {
    super(cause);
  }

  /**
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public DataExporterException(String message, Throwable cause) {
    super(message, cause);
  }
}
