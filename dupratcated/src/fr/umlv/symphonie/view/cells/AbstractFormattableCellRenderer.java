/*
 * This file is part of Symphonie
 * Created on 23 mars 2005
 */

package fr.umlv.symphonie.view.cells;

import java.util.HashMap;

import javax.swing.table.DefaultTableCellRenderer;

/**
 * Abstract implementation of the FormattableCellRenderer interface.
 * 
 * @author spenasal
 */
public abstract class AbstractFormattableCellRenderer implements
    FormattableCellRenderer {

  /** List of formatted objects */
  protected final HashMap<Object, CellFormat> fObjects = new HashMap<Object, CellFormat>();

  /** Object that can be used in subclasses to delegate TableCellRenderer methods */
  protected final DefaultTableCellRenderer render = new DefaultTableCellRenderer();

  public void addFormatedObject(Object o, CellFormat format) {
    if (o == null || format == null) return;
    fObjects.put(o, format);
  }

  public CellFormat removeFormatedObject(Object o) {
    return fObjects.remove(o);
  }

  public boolean hasFormat(Object o) {
    if (o == null) return false;
    return fObjects.containsKey(o);
  }
}
