/*
 * This file is part of Symphonie
 * Created : 17 févr. 2005 19:48:30
 */

package fr.umlv.symphonie.util.wizard;

import javax.swing.Icon;

import fr.umlv.symphonie.util.wizard.event.WizardListener;

/**
 * Suitable data model for Wizards. The application that is interested in having
 * wizards implements this interface.
 */
public interface WizardModel {

  /**
   * Gets the default title for wizard dialog
   * 
   * @return The wizard title
   */
  public String getWizardTitle();

  /**
   * Gets the text that will be used for the cancel button
   * 
   * @return the cancel button text
   */
  public String getCancelButtonText();

  /**
   * Gets the text that will be used for the finish button
   * 
   * @return the finish button text
   */
  public String getFinishButtonText();

  /**
   * Gets the text that will be used for the help button
   * 
   * @return the help button text
   */
  public String getHelpButtonText();

  /**
   * Gets the text that will be used for the next button
   * 
   * @return the next button text
   */
  public String getNextButtonText();

  /**
   * Gets the text that will be used for the previous button
   * 
   * @return the previous button text
   */
  public String getPreviousButtonText();

  /**
   * Gets the model current panel
   * 
   * @return the current panel
   */
  public WizardPanel getCurrentPanel();

  /**
   * Gets the model <code>Icon</code>
   * 
   * @return the <code>Icon</code> object for this model
   */
  public Icon getWizardIcon();

  /**
   * Moves to next panel <br>
   * All listeners registered so far will receive a notification of the change
   * 
   * @throws RuntimeException
   *           if there's no more panels where to go or current panel hasn't
   *           been validated
   */
  public void moveToNextPanel();

  /**
   * Moves to previous panel <br>
   * All listeners registered so far will receive a notification of the change
   * 
   * @throws RuntimeException
   *           if there no panel where to move back
   */
  public void moveToPreviousPanel();

  /**
   * Returns whether model can move to next panel without throwing an exception
   * 
   * @return true if model has more panels
   */
  public boolean hasNextPanel();

  /**
   * Returns whether model can move to previous panel without throwing an
   * exception
   * 
   * @return true if current panel is not the first one
   */
  public boolean hasPreviousPanel();

  /**
   * Returns whether the given panel can be the last one
   * 
   * @param panel
   *          The panel to check
   * @return true if panel can finish the wizard
   */
  public boolean canFinish(WizardPanel panel);

  /**
   * Returns whether the given panel can display help
   * 
   * @param panel
   *          The panel to check
   * @return true if panel has help
   */
  public boolean hasHelp(WizardPanel panel);

  /**
   * Finishes wizard model <br>
   * Calls all listeners registered to the finish event
   */
  public void finish();

  /**
   * Cancels wizard model <br>
   * Calls all listeners registered to the cancel event
   */
  public void cancel();

  /**
   * Starts wizard model from the first panel <br>
   * Calls all listeners registered to the starting event
   */
  public void start();

  /**
   * Adds a listener to the wizard that's notified each time a change to the
   * wizard model occurs
   * 
   * @param wl
   *          The listener to be added
   */
  public void addWizardListener(WizardListener wl);

  /**
   * Removes a listener so that it won't be notified anymore each time a change
   * to the wizard model occurs
   * 
   * @param wl
   *          The listener to be removed
   */
  public void removeWizardListener(WizardListener wl);

}
