
package fr.umlv.symphonie.data;

public class DataManagerException extends Exception {

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3256443616326006576L;

  public DataManagerException(String message) {
    super(message);
  }

  public DataManagerException(Throwable cause) {
    super(cause);
  }

  public DataManagerException(String message, Throwable cause) {
    super(message, cause);
  }
}
