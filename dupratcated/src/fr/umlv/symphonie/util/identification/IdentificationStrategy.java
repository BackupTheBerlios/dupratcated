/*
 * This file is part of Symphonie
 * Created : 20 mars 2005 19:39:58
 */

package fr.umlv.symphonie.util.identification;

import javax.swing.event.ChangeListener;

/**
 * Interface defines an identification service for a single user.
 */
public interface IdentificationStrategy {

  /**
   * Identifies the user this identification service was created for. <br>
   * This method may be blocking according to implementations. After this method
   * call the user is successfully identified unless an exception is thrown.
   * Must prevent listeners of login success.
   * 
   * @param password
   *          The user password
   * @throws IdentificationException
   *           If identification fail by any means
   */
  public void identify(String password) throws IdentificationException;

  /**
   * Identifies the user this identification service was created for. <br>
   * Routine should be not blocking, the user may not be identified after this
   * method call. <br>
   * Must prevent listeners if login successes
   * 
   * @param password
   *          The user password
   */
  public void identifyNow(String password);

  /**
   * Tells whether the user is identified or not
   * 
   * @return <code>true</code> if the user is correctly identified,
   *         <code>false</code> otherwise
   */
  public boolean isIdentified();

  /**
   * Adds a change listener to the listeners list. <br>
   * Listeners are prevented of changes of the login state.
   * 
   * @param l
   *          The listener to add
   */
  public void addChangeListener(ChangeListener l);

  /**
   * Logs the user out. <br>
   * Must prevent all listener of the unidentification
   * 
   * @throws IdentificationException
   *           If there's any problem
   */
  public void logout() throws IdentificationException;

  /**
   * Sets a new password for the user. <br>
   * This operation logs the user out, he has to identify again, but with the
   * new password.
   * 
   * @param oldPassword
   *          The old password
   * @param newPassword
   *          The new password
   * @throws IdentificationException
   *           If anything fails
   */
  public void changePassword(String oldPassword, String newPassword)
      throws IdentificationException;
}
