/*
 * This file is part of Symphonie
 * Created : 22 févr. 2005 18:15:21
 */

package fr.umlv.symphonie.util;

import java.util.Collection;
import java.util.HashMap;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolTip;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Builder for runtime text-changing Swing component.
 */
public class ComponentBuilder {

  /**
   * Class field containing names for <code>Component</code> s built by
   * <code>this</code>
   */
  private HashMap<String, String> nameMap;

  /**
   * Listeners list
   */
  private final HashMap<JComponent, ChangeListener> listenersMap = new HashMap<JComponent, ChangeListener>();

  /**
   * Event used when <code>this</code> state has changed
   */
  private final ChangeEvent builderChanged;

  /**
   * Creates a new SymphonieComponentBuilder using the give name map.
   * 
   * @param nameMap
   *          The name map to use
   */
  public ComponentBuilder(HashMap<String, String> nameMap) {
    this.nameMap = nameMap;
    this.builderChanged = new ChangeEvent(this);
  }

  public HashMap<String, String> getNameMap() {
    return nameMap;
  }

  /**
   * Sets a new name map and notifies all listener that state has changed
   * 
   * @param nameMap
   *          The new name map to use
   */
  public void setNameMap(HashMap<String, String> nameMap) {
    this.nameMap = nameMap;
    Collection<ChangeListener> listeners = listenersMap.values();
    for (ChangeListener cl : listeners)
      cl.stateChanged(builderChanged);
  }

  /**
   * Returns the associated textual resource for the given key
   * 
   * @param key
   *          The key
   * @return The resource or null if there's no resource bound to the given key
   */
  public String getValue(String key) {
    return nameMap.get(key);
  }

  /**
   * The specified change listener used by component, so that listener receive
   * events from this builder. If listener or component is null, no exception is
   * thrown and no action is performed
   * 
   * @param component
   *          The component
   * @param listener
   *          The listener associated to component
   */
  public void addChangeListener(JComponent component, ChangeListener listener) {
    if (listener != null && component != null)
      listenersMap.put(component, listener);
  }

  /**
   * Removes the specified listener associated to the given component so that it
   * no longer receives events from this builder. This method performs no
   * function, nor does it throw an exception, if the component specified by the
   * argument was not previously added to this builder. If component is null, no
   * exception is thrown and no action is performed.
   * 
   * @param component
   *          The component associated to the listener
   */
  public void removeChangeListener(JComponent component) {
    if (component != null) listenersMap.remove(component);
  }

  /**
   * Builds a JLabel with the text given by <code>getValue(key)</code><br>
   * A ChangeListener is automatically created and registered to the builder.
   * 
   * @param key
   *          The key
   * @return The label built
   */
  public JLabel buildLabel(final String key) {
    final JLabel label = new JLabel(getValue(key));
    addChangeListener(label, new ChangeListener() {

      public void stateChanged(ChangeEvent event) {
        label.setText(getValue(key));
      }
    });
    return label;
  }

  /**
   * Builds a JToolTip for the given component with the text given by
   * <code>getValue(key)</code><br>
   * A ChangeListener is automatically created and registered to the builder for
   * the tool tip.
   * 
   * @param key
   *          The key
   * @param c
   *          The component that will use the tip
   * @return The tool tip created
   */
  public JToolTip buildToolTip(final String key, JComponent c) {
    final JToolTip tip = new JToolTip();
    tip.setTipText(getValue(key));
    tip.setComponent(c);
    addChangeListener(tip, new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        tip.setTipText(getValue(key));
      }
    });
    return tip;
  }

  /**
   * Builds an empty JPopupMenu with the text given by
   * <code>getValue(key)</code><br>
   * 
   * @see #buildLabel(String)
   * @param key
   *          The key
   * @return The pop-up menu built
   */
  public JPopupMenu buildPopupMenu(final String key) {
    final JPopupMenu menu = new JPopupMenu(getValue(key));
    addChangeListener(menu, new ChangeListener() {

      public void stateChanged(ChangeEvent arg0) {
        menu.setLabel(getValue(key));
      }
    });
    return menu;
  }

  /**
   * Builds a JButton with the text given by <code>getValue(key)</code> A
   * ChangeListener is automatically created and registered to the builder.
   * 
   * @param key
   *          The key for the text
   * @return The button built
   */
  public AbstractButton buildButton(final String key, ButtonType type) {
    final AbstractButton b = type.getButton(getValue(key));
    addChangeListener(b, new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        b.setText(getValue(key));
      }
    });
    return b;
  }

  /**
   * Builds a JButton with the text given by <code>getValue(key)</code> and
   * the <code>Action</code> supplied <br>
   * A ChangeListener is automatically created and registered to the builder. If
   * you button is an icon-only button usage of builder is discouraged
   * 
   * @param a
   *          The Action where button properties will be taken from
   * @param key
   *          The key for the text
   * @return The button built
   */
  public AbstractButton buildButton(final Action action, final String key,
      ButtonType type) {
    action.putValue(Action.NAME, getValue(key));
    AbstractButton button = type.getButton(action);
    addChangeListener(button, new ChangeListener() {

      public void stateChanged(ChangeEvent event) {
        action.putValue(Action.NAME, getValue(key));
      }
    });
    return button;
  }

  /**
   * Enumeration defines types for buttons created by <code>buildButton</code>:
   * <br>
   * <li>BUTTON : JButton
   * <li>CHECK_BOX : JCheckBox
   * <li>RADIO_BUTTON : JRadioButton
   * <li>MENU_ITEM : JMenuItem
   * <li>MENU : JMenuItem
   * <li>CHECK_BOX_MENU_ITEM : JCheckBoxMenuItem
   * <li>RADIO_BUTTON_MENU_ITEM : JRadioButtonMenuItem
   * 
   * @see ComponentBuilder#buildButton(Action, String, ButtonType)
   * @see ComponentBuilder#buildButton(String, ButtonType)
   */
  public enum ButtonType {
    BUTTON {

      AbstractButton getButton(Action action) {
        return new JButton(action);
      }

      AbstractButton getButton(String text) {
        return new JButton(text);
      }
    },
    CHECK_BOX {

      AbstractButton getButton(Action action) {
        return new JCheckBox(action);
      }

      AbstractButton getButton(String text) {
        return new JCheckBox(text);
      }
    },
    RADIO_BUTTON {

      AbstractButton getButton(Action action) {
        return new JRadioButton(action);
      }

      AbstractButton getButton(String text) {
        return new JRadioButton(text);
      }
    },
    MENU_ITEM {

      AbstractButton getButton(Action action) {
        return new JMenuItem(action);
      }

      AbstractButton getButton(String text) {
        return new JMenuItem(text);
      }
    },
    MENU {

      AbstractButton getButton(Action action) {
        return new JMenu(action);
      }

      AbstractButton getButton(String text) {
        return new JMenu(text);
      }
    },
    CHECK_BOX_MENU_ITEM {

      AbstractButton getButton(Action action) {
        return new JCheckBoxMenuItem(action);
      }

      AbstractButton getButton(String text) {
        return new JCheckBoxMenuItem(text);
      }
    },
    RADIO_BUTTON_MENU_ITEM {

      AbstractButton getButton(Action action) {
        return new JRadioButtonMenuItem(action);
      }

      AbstractButton getButton(String text) {
        return new JRadioButtonMenuItem(text);
      }
    };

    /**
     * Creates a button where properties are taken from the Action supplied
     * 
     * @param action
     *          the Action used to specify the new button
     * @return The button created
     */
    abstract AbstractButton getButton(Action action);

    /**
     * Creates a button with the text supplied
     * 
     * @param text
     *          The text
     * @return The button created
     */
    abstract AbstractButton getButton(String text);
  }
}
