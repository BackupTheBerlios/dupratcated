/*
 * This file is part of Symphonie
 * Created : 17 févr. 2005 19:58:03
 */

package fr.umlv.symphonie.util.wizard.event;

/**
 * The listener interface for receiving events from wizard model. The class that
 * is interested in processing a wizard event implements this interface.
 */
public interface WizardListener {

  /**
   * Invoked when current panel has changed
   */
  public void currentPanelChanged(WizardEvent we);

  /**
   * Invoked when wizard is finishing
   */
  public void wizardFinished(WizardEvent we);

  /**
   * Invoked when wizard is cancelled
   */
  public void wizardCancelled(WizardEvent we);
}
