/*
 * This file is part of Symphonie
 * Created : 8 mars 2005 14:44:27
 */

package fr.umlv.symphonie.view.cells;

import java.awt.Color;

import fr.umlv.symphonie.data.formula.BooleanFormula;

/**
 * Format of a cell in the Symphonie object-formatting model
 * 
 * @author PEÑA SALDARRIAGA Sébastian
 */
public final class CellFormat {

  /** Condition that the format depends on */
  private BooleanFormula condition;

  /** Cell's background color */
  private Color background;

  /** Cell's foreground color */
  private Color foreground;

  /**
   * Creates a new instance with the given condition and colors.
   * 
   * @param condition
   *          Condition that the format depends on
   * @param background
   *          Cell's background color
   * @param foreground
   *          Cell's foreground color
   */
  public CellFormat(BooleanFormula condition, Color background, Color foreground) {
    this.condition = condition;
    this.background = background;
    this.foreground = foreground;
  }

  public Color getBackground() {
    return background;
  }

  public void setBackground(Color background) {
    this.background = background;
  }

  public BooleanFormula getCondition() {
    return condition;
  }

  public void setCondition(BooleanFormula condition) {
    this.condition = condition;
  }

  public Color getForeground() {
    return foreground;
  }

  public void setForeground(Color foreground) {
    this.foreground = foreground;
  }

  public String toString() {
    return "[" + condition.toString() + " | " + foreground + " | " + background
        + "]";
  }
}
