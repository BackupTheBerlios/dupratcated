/*
 * This file is part of Symphonie
 * Created : 17 févr. 2005 20:13:46
 */

package fr.umlv.symphonie.util.wizard;

import java.util.ArrayList;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Class used for implementing a panel that is displayed in a
 * <code>Wizard</code>
 */
public abstract class WizardPanel {

  /**
   * Data for panel
   */
  protected Map<Object, Object> data;

  /**
   * @see #setData(Map)
   * @return This object's data
   */
  public Map<Object, Object> getData() {
    return data;
  }

  /**
   * Sets the data for the panel <br>
   * If several wizard panels need to share data this is the way to do it.
   * Implementations are responsible for adding the data that will be shared
   * between multiple panel instances. <br>
   * <code>WizarModel</code> implementation must ensure a call to this method
   * before performing any action on the panel. <br>
   * 
   * @param data
   *          The data for the panel
   */
  public void setData(Map<Object, Object> data) {
    this.data = data;
  }

  /**
   * Gets the title of this panel
   * 
   * @return The title of this panel
   */
  public abstract String getTitle();

  /**
   * Gets the description of the panel
   * 
   * @return The description of this panel
   */
  public abstract String getDescription();

  /**
   * Returns the <code>JComponent</code> that represents the panel
   * 
   * @return The JComponent that serves as panel
   */
  public abstract JComponent getPanelComponent();

  /**
   * Method called to validate the panel before moving to the next one
   * 
   * @return true if we can move to next panel
   */
  public abstract boolean isValid();

  /**
   * Method called to know if this panel can finis the wizard
   * 
   * @return true if panel can finish the wizard or false otherwise
   */
  public abstract boolean canFinish();

  /**
   * Method called to know if this panel has help <br>
   * Override this method if your panel provide help
   * 
   * @return false
   */
  public boolean hasHelp() {
    return false;
  }

  /**
   * Should display panel help <br>
   * Override if your panel supports help
   */
  public void help() {
  }

  /**
   * Adds a listener that will be notified of state changes of panel
   * 
   * @param cl
   *          The listener to add
   */
  public void addChangeListener(ChangeListener cl) {
    listeners.add(cl);
  }

  /**
   * Removes a listener so that it won't be notified of state changes from panel
   * 
   * @param cl
   *          The listener to remove
   */
  public void removeChangeListener(ChangeListener cl) {
    listeners.remove(cl);
  }

  /**
   * Notifies all listeners that panel has changed
   */
  public void firePanelChanged() {
    for (int i = listeners.size() - 1; i >= 0; i--)
      listeners.get(i).stateChanged(event);
  }

  /**
   * Listeners list
   */
  private final ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();

  /**
   * Panel change event
   */
  private final ChangeEvent event = new ChangeEvent(this);
}
