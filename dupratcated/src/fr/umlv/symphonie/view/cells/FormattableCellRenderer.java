/*
 * This file is part of Symphonie
 * Created on 23 mars 2005
 */

package fr.umlv.symphonie.view.cells;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 * Interface defines a cell renderer that allows conditional formatting of
 * single objects
 * 
 * @author spenasal
 */
public interface FormattableCellRenderer extends TableCellRenderer {

  /**
   * Adds a format to an object
   * 
   * @param o
   *          The object to format
   * @param format
   *          The format
   */
  public void addFormatedObject(Object o, CellFormat format);

  /**
   * Removes the format of an object
   * 
   * @param o
   *          The object
   * @return The format that was associated to the object
   */
  public CellFormat removeFormatedObject(Object o);

  /**
   * Returns the model associated to this <code>FormattableCellRenderer</code>
   * 
   * @return a <code>TableModel</code>
   */
  public TableModel getModel();
}
