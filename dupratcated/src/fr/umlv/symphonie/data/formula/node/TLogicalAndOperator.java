/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class TLogicalAndOperator extends Token {

  public TLogicalAndOperator() {
    super.setText("&&");
  }

  public TLogicalAndOperator(int line, int pos) {
    super.setText("&&");
    setLine(line);
    setPos(pos);
  }

  public Object clone() {
    return new TLogicalAndOperator(getLine(), getPos());
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseTLogicalAndOperator(this);
  }

  public void setText(String text) {
    throw new RuntimeException("Cannot change TLogicalAndOperator text.");
  }
}
