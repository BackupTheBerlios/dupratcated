/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class AEqualityComparativeExpression extends
    PComparativeExpression {

  private PEqualityExpression _equalityExpression_;

  public AEqualityComparativeExpression() {
  }

  public AEqualityComparativeExpression(PEqualityExpression _equalityExpression_) {
    setEqualityExpression(_equalityExpression_);

  }

  public Object clone() {
    return new AEqualityComparativeExpression(
        (PEqualityExpression) cloneNode(_equalityExpression_));
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseAEqualityComparativeExpression(this);
  }

  public PEqualityExpression getEqualityExpression() {
    return _equalityExpression_;
  }

  public void setEqualityExpression(PEqualityExpression node) {
    if (_equalityExpression_ != null) {
      _equalityExpression_.parent(null);
    }

    if (node != null) {
      if (node.parent() != null) {
        node.parent().removeChild(node);
      }

      node.parent(this);
    }

    _equalityExpression_ = node;
  }

  public String toString() {
    return "" + toString(_equalityExpression_);
  }

  void removeChild(Node child) {
    if (_equalityExpression_ == child) {
      _equalityExpression_ = null;
      return;
    }

  }

  void replaceChild(Node oldChild, Node newChild) {
    if (_equalityExpression_ == oldChild) {
      setEqualityExpression((PEqualityExpression) newChild);
      return;
    }

  }
}
