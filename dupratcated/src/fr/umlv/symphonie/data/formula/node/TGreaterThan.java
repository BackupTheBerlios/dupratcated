/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class TGreaterThan extends Token {

  public TGreaterThan() {
    super.setText(">");
  }

  public TGreaterThan(int line, int pos) {
    super.setText(">");
    setLine(line);
    setPos(pos);
  }

  public Object clone() {
    return new TGreaterThan(getLine(), getPos());
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseTGreaterThan(this);
  }

  public void setText(String text) {
    throw new RuntimeException("Cannot change TGreaterThan text.");
  }
}