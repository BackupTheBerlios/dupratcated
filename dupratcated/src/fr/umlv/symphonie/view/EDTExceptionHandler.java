package fr.umlv.symphonie.view;

import fr.umlv.symphonie.util.ExceptionDisplayDialog;

/**
 * Class for handling uncatched EventDispatchThread exceptions
 * 
 * @author spenasal
 */
public final class EDTExceptionHandler {

  /**
   * Function that handles exception
   * 
   * @param t
   *          The exception thrown
   */
    public void handle(Throwable t) {
    System.out.println("tarass");
      if (errHandler != null)
      synchronized (lock) {
        errHandler.showException(t);
      }
    else
      t.printStackTrace(System.out);
  }
  
  /**
   * Default constructor
   */
  public EDTExceptionHandler() {   
  }

  /**
   * The internal exception displayer, shared by all instances
   */
  static ExceptionDisplayDialog errHandler;

  /**
   * Lock for synchronizing accesses
   */
  static final Object lock = new Object();

  /**
   * Sets the internal exception displayer
   * 
   * @param dialog
   *          The dialog for displaying exceptions, if null routine does
   *          nothing.
   */
  static void setDisplayer(ExceptionDisplayDialog dialog) {
    if (dialog != null) synchronized (lock) {
      errHandler = dialog;
    }
  }
}