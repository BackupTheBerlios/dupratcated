/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class ADoubleLiteralExpression extends PLiteralExpression {

  private TDoubleLiteral _doubleLiteral_;

  public ADoubleLiteralExpression() {
  }

  public ADoubleLiteralExpression(TDoubleLiteral _doubleLiteral_) {
    setDoubleLiteral(_doubleLiteral_);

  }

  public Object clone() {
    return new ADoubleLiteralExpression(
        (TDoubleLiteral) cloneNode(_doubleLiteral_));
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseADoubleLiteralExpression(this);
  }

  public TDoubleLiteral getDoubleLiteral() {
    return _doubleLiteral_;
  }

  public void setDoubleLiteral(TDoubleLiteral node) {
    if (_doubleLiteral_ != null) {
      _doubleLiteral_.parent(null);
    }

    if (node != null) {
      if (node.parent() != null) {
        node.parent().removeChild(node);
      }

      node.parent(this);
    }

    _doubleLiteral_ = node;
  }

  public String toString() {
    return "" + toString(_doubleLiteral_);
  }

  void removeChild(Node child) {
    if (_doubleLiteral_ == child) {
      _doubleLiteral_ = null;
      return;
    }

  }

  void replaceChild(Node oldChild, Node newChild) {
    if (_doubleLiteral_ == oldChild) {
      setDoubleLiteral((TDoubleLiteral) newChild);
      return;
    }

  }
}
