/*
 * This file is part of Symphonie
 * Created : 13 févr. 2005 10:43:06
 */

package fr.umlv.symphonie.util;

import java.util.Collection;
import java.util.HashMap;

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
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Builder for runtime text-changing Swing component.
 */
public final class ComponentBuilder {

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
   * Convenient method for creating ChangeListener from actions used by JButtons
   * 
   * @param action
   * @param key
   * @return
   */
  private ChangeListener createPutNameListener(final Action action,
      final String key) {
    return new ChangeListener() {

      public void stateChanged(ChangeEvent event) {
        action.putValue(Action.NAME, getValue(key));
      }
    };
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
  public JButton buildButton(Action action, String key) {
    action.putValue(Action.NAME, getValue(key));
    JButton button = new JButton(action);
    addChangeListener(button, createPutNameListener(action, key));
    return button;
  }

  /**
   * Builds an empty JMenu with the text given by <code>getValue(key)</code>
   * <br>
   * 
   * @see #buildLabel(String)
   * @param key
   *          The key
   * @return The menu built
   */
  public JMenu buildMenu(final String key) {
    final JMenu menu = new JMenu(getValue(key));
    addChangeListener(menu, new ChangeListener() {

      public void stateChanged(ChangeEvent arg0) {
        menu.setText(getValue(key));
      }
    });
    return menu;
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
   * Builds a JMenuItem of the given type with the text given by
   * <code>getValue(key)</code> and the <code>Action</code> supplied <br>
   * 
   * @see #buildButton(Action, String)
   * @see #JMenuItemType
   * @param a
   *          The Action where menu item properties will be taken from
   * @param key
   *          The key for the text
   * @param type
   *          The type of menu item to be created
   * @return The menu item built
   */
  public JMenuItem buildMenuItem(Action action, String key, JMenuItemType type) {
    action.putValue(Action.NAME, getValue(key));
    JMenuItem item = type.getMenuItem(action);
    addChangeListener(item, createPutNameListener(action, key));
    return item;
  }

  /**
   * Builds a JToggleButton of the given type with the text given by
   * <code>getValue(key)</code> and the <code>Action</code> supplied <br>
   * 
   * @see #buildButton(Action, String)
   * @see #JToggleButtonType
   * @param a
   *          The Action where button properties will be taken from
   * @param key
   *          The key for the text
   * @param type
   *          The type of the toggle button
   * @param selected
   *          If true, the button is initially selected; otherwise, the button
   *          is initially unselected
   * @return The toggle button created
   */
  public JToggleButton buildToggleButton(Action action, String key,
      JToggleButtonType type, boolean selected) {
    action.putValue(Action.NAME, getValue(key));
    JToggleButton toggle = type.getToggleButton(action);
    toggle.setSelected(selected);
    addChangeListener(toggle, createPutNameListener(action, key));
    return toggle;
  }

  /**
   * Enumeration defines a type for JToggleButtons :<br>
   * <li>CHECK_BOX
   * <li>RADIO_BUTTON
   */
  public enum JToggleButtonType {
    CHECK_BOX {

      JToggleButton getToggleButton(Action action) {
        return new JCheckBox(action);
      }
    }, RADIO_BUTTON {

      JToggleButton getToggleButton(Action action) {
        return new JRadioButton(action);
      }
    }
    ;

    /**
     * Creates a toggle button where properties are taken from the Action
     * supplied
     * 
     * @param action
     *          the Action used to specify the new toggle button
     * @return The button created
     */
    abstract JToggleButton getToggleButton(Action action);
  }

  /**
   * Enumeration defines a type for JMenuItems :<br>
   * <li>NORMAL_ITEM
   * <li>CHECK_BOX_ITEM
   * <li>RADIO_BUTTON_ITEM
   */
  public enum JMenuItemType {
    NORMAL_ITEM {

      JMenuItem getMenuItem(Action action) {
        return new JMenuItem(action);
      }
    }, CHECK_BOX_ITEM {

      JMenuItem getMenuItem(Action action) {
        return new JCheckBoxMenuItem(action);
      }
    }, RADIO_BUTTON_ITEM {

      JMenuItem getMenuItem(Action action) {
        return new JRadioButtonMenuItem(action);
      }
    }
    ;

    /**
     * Creates a menu item where properties are taken from the Action supplied
     * 
     * @param action
     *          the Action used to specify the new menu item
     * @return The menu item created
     */
    abstract JMenuItem getMenuItem(Action action);
  }
}
