/*
 * This file is part of Symphonie
 * Created : 20 mars 2005 19:54:51
 */

package fr.umlv.symphonie.util.identification;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Abstract implementation of an <code>IndentificationStrategy</code>.<br>
 * Implements listeners support
 */
public abstract class AbstractIndentificationStrategy implements
    IndentificationStrategy {

  /** Listeners list */
  protected final ArrayList<ChangeListener> listeners = new ArrayList<ChangeListener>();

  /** The event fired when state changes */
  protected ChangeEvent event = new ChangeEvent(this);

  public void addChangeListener(ChangeListener l) {
    if (!listeners.contains(l)) listeners.add(l);
  }

  /**
   * Notifies all listeners of a state change
   */
  protected void fireStateChanged() {
    for (ChangeListener listener : listeners)
      listener.stateChanged(event);
  }
}
