/*
 * This file is part of Symphonie
 * Created : 17 févr. 2005 20:15:23
 */

package fr.umlv.symphonie.util.wizard.event;

import java.util.EventObject;

import fr.umlv.symphonie.util.wizard.WizardModel;
import fr.umlv.symphonie.util.wizard.WizardPanel;

/**
 * Event class for wizard model state changes
 */
public class WizardEvent extends EventObject {

  /**
   * The panel that the event came from
   */
  private WizardPanel panel;

  /**
   * Creates a <code>WizardEvent</code> object
   * 
   * @param source
   *          The wizard model source of the event
   */
  public WizardEvent(WizardModel source, WizardPanel panel) {
    super(source);
    this.panel = panel;
  }

  /**
   * Returns the panel that was displaying when event occurred
   * 
   * @return a wizard panel
   */
  public WizardPanel getPanel() {
    return panel;
  }
}
