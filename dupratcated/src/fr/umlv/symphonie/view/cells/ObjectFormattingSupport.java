/*
 * This file is part of Symphonie
 * Created on 23 mars 2005
 */

package fr.umlv.symphonie.view.cells;

/**
 * Interfaces that any object that allows object conditional formatting must
 * implement
 * 
 * @author spenasal
 */
public interface ObjectFormattingSupport {

  /**
   * Returns the FormattableCellRendere object associated to the target object
   * 
   * @return a <code>FormattableCellRenderer</code>
   */
  public FormattableCellRenderer getFormattableCellRenderer();
}
