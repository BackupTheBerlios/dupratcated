/*
 * This file is part of Symphonie
 * Created : 17 févr. 2005 20:58:44
 */

package fr.umlv.symphonie.util.wizard.event;

/**
 * An abstract adapter class for receiving wizard events. The methods in this
 * class are empty. This class exists as convenience for creating listener
 * objects.
 */
public class WizardListenerAdapter implements WizardListener {

  public void currentPanelChanged(WizardEvent we) {
  }

  public void wizardFinished(WizardEvent we) {
  }

  public void wizardCancelled(WizardEvent we) {
  }
}
