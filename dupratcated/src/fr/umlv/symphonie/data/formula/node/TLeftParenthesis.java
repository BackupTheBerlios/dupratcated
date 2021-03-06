/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class TLeftParenthesis extends Token {

  public TLeftParenthesis() {
    super.setText("(");
  }

  public TLeftParenthesis(int line, int pos) {
    super.setText("(");
    setLine(line);
    setPos(pos);
  }

  public Object clone() {
    return new TLeftParenthesis(getLine(), getPos());
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseTLeftParenthesis(this);
  }

  public void setText(String text) {
    throw new RuntimeException("Cannot change TLeftParenthesis text.");
  }
}
