/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class TIdSeparator extends Token {

  public TIdSeparator() {
    super.setText(",");
  }

  public TIdSeparator(int line, int pos) {
    super.setText(",");
    setLine(line);
    setPos(pos);
  }

  public Object clone() {
    return new TIdSeparator(getLine(), getPos());
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseTIdSeparator(this);
  }

  public void setText(String text) {
    throw new RuntimeException("Cannot change TIdSeparator text.");
  }
}
