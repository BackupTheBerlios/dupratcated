/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class AUnaryMultiplicativeExpression extends
    PMultiplicativeExpression {

  private PUnaryExpression _unaryExpression_;

  public AUnaryMultiplicativeExpression() {
  }

  public AUnaryMultiplicativeExpression(PUnaryExpression _unaryExpression_) {
    setUnaryExpression(_unaryExpression_);

  }

  public Object clone() {
    return new AUnaryMultiplicativeExpression(
        (PUnaryExpression) cloneNode(_unaryExpression_));
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseAUnaryMultiplicativeExpression(this);
  }

  public PUnaryExpression getUnaryExpression() {
    return _unaryExpression_;
  }

  public void setUnaryExpression(PUnaryExpression node) {
    if (_unaryExpression_ != null) {
      _unaryExpression_.parent(null);
    }

    if (node != null) {
      if (node.parent() != null) {
        node.parent().removeChild(node);
      }

      node.parent(this);
    }

    _unaryExpression_ = node;
  }

  public String toString() {
    return "" + toString(_unaryExpression_);
  }

  void removeChild(Node child) {
    if (_unaryExpression_ == child) {
      _unaryExpression_ = null;
      return;
    }

  }

  void replaceChild(Node oldChild, Node newChild) {
    if (_unaryExpression_ == oldChild) {
      setUnaryExpression((PUnaryExpression) newChild);
      return;
    }

  }
}
