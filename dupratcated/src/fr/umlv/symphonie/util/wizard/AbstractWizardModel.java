/*
 * This file is part of Symphonie
 * Created : 17 févr. 2005 21:19:35
 */

package fr.umlv.symphonie.util.wizard;

import java.util.ArrayList;

import fr.umlv.symphonie.util.wizard.event.WizardEvent;
import fr.umlv.symphonie.util.wizard.event.WizardListener;

/**
 * Abstract implementation for the data model that provides a
 * <code>Wizard</code> with its contents
 */
public abstract class AbstractWizardModel implements WizardModel {

  /**
   * Listeners list
   */
  private final ArrayList<WizardListener> listeners = new ArrayList<WizardListener>();

  /**
   * Default constructor
   */
  public AbstractWizardModel() {
  }

  public WizardPanel getCurrentPanel() {
    return null;
  }

  public boolean hasNextPanel() {
    return false;
  }

  public boolean hasPreviousPanel() {
    return false;
  }

  public boolean canFinish(WizardPanel panel) {
    return false;
  }

  public boolean hasHelp(WizardPanel panel) {
    return false;
  }

  public void addWizardListener(WizardListener wl) {
    listeners.add(wl);
  }

  public void removeWizardListener(WizardListener wl) {
    listeners.remove(wl);
  }

  /**
   * Notifies all listeners that current panel has changed
   * 
   * @param we
   *          The event object
   */
  protected void fireCurrentPanelChanged(WizardEvent we) {
    for (int i = listeners.size() - 1; i >= 0; i--)
      listeners.get(i).currentPanelChanged(we);
  }

  public void cancel() {
    WizardEvent ev = new WizardEvent(this, getCurrentPanel());
    for (int i = listeners.size() - 1; i >= 0; i--)
      listeners.get(i).wizardCancelled(ev);
  }

  public void finish() {
    WizardEvent ev = new WizardEvent(this, getCurrentPanel());
    for (int i = listeners.size() - 1; i >= 0; i--)
      listeners.get(i).wizardFinished(ev);
  }
}
