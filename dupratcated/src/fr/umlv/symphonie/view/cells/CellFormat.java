/*
 * This file is part of Symphonie
 * Created : 8 mars 2005 14:44:27
 */

package fr.umlv.symphonie.view.cells;

import java.awt.Color;

import fr.umlv.symphonie.data.formula.BooleanFormula;

public final class CellFormat {

  private BooleanFormula condition;
  private Color background;
  private Color foreground;

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
}
