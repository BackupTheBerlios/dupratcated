/*
 * This file is part of Symphonie
 * Created : 20 mars 2005 20:05:51
 */
package fr.umlv.symphonie.util.identification;


/**
 * SQL based implementation of an <code>IdentificationStrategy</code>
 */
public final class SQLIdentificationStrategy extends AbstractIndentificationStrategy {

  private boolean loggedIn;
  
  public void identify(String password) throws IdentificationException {
  }

  /**
   * Throws an <code>UnsupportedOperationException</code>
   */
  public void identifyNow(String password) {
    throw new UnsupportedOperationException("Implementation doesn't support non blocking identification");
  }

  public boolean isIdentified() {
    return loggedIn;
  }

  public void logout() {
  }

  public void changePassword(String oldPassword, String newPassword) throws IdentificationException {
  }
}
