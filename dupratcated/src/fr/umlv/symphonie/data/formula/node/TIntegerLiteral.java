/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class TIntegerLiteral extends Token {

  public TIntegerLiteral(String text) {
    setText(text);
  }

  public TIntegerLiteral(String text, int line, int pos) {
    setText(text);
    setLine(line);
    setPos(pos);
  }

  public Object clone() {
    return new TIntegerLiteral(getText(), getLine(), getPos());
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseTIntegerLiteral(this);
  }
}