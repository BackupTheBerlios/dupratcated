/*
 * This file is part of Symphonie
 * Created : 20 mars 2005 19:43:43
 */

package fr.umlv.symphonie.util.identification;

/**
 * Exception thrown by <code>IdentificationStrategy</code> methods
 */
public class IdentificationException extends Exception {

  /**
   * Creates a new <code>IdentificationException</code> with the given message
   * 
   * @param message
   *          The exception message
   */
  public IdentificationException(String message) {
    super(message);
  }

  /**
   * Creates a new <code>IdentificationException</code> with the given message
   * and cause
   * 
   * @param message
   *          The exception message
   * @param cause
   *          The exception cause
   */
  public IdentificationException(String message, Throwable cause) {
    super(message, cause);
  }
}
