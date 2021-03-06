/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class TBlockPrefix extends Token {

  public TBlockPrefix() {
    super.setText("$");
  }

  public TBlockPrefix(int line, int pos) {
    super.setText("$");
    setLine(line);
    setPos(pos);
  }

  public Object clone() {
    return new TBlockPrefix(getLine(), getPos());
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseTBlockPrefix(this);
  }

  public void setText(String text) {
    throw new RuntimeException("Cannot change TBlockPrefix text.");
  }
}
