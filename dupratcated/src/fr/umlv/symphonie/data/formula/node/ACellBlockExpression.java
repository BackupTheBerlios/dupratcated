/* This file was generated by SableCC (http://www.sablecc.org/). */

package fr.umlv.symphonie.data.formula.node;

import fr.umlv.symphonie.data.formula.analysis.Analysis;

public final class ACellBlockExpression extends PBlockExpression {

  private TBlockStart _blockStart_;
  private PIdentifiers _identifiers_;
  private TBlockEnd _blockEnd_;

  public ACellBlockExpression() {
  }

  public ACellBlockExpression(TBlockStart _blockStart_,
      PIdentifiers _identifiers_, TBlockEnd _blockEnd_) {
    setBlockStart(_blockStart_);

    setIdentifiers(_identifiers_);

    setBlockEnd(_blockEnd_);

  }

  public Object clone() {
    return new ACellBlockExpression((TBlockStart) cloneNode(_blockStart_),
        (PIdentifiers) cloneNode(_identifiers_),
        (TBlockEnd) cloneNode(_blockEnd_));
  }

  public void apply(Switch sw) {
    ((Analysis) sw).caseACellBlockExpression(this);
  }

  public TBlockStart getBlockStart() {
    return _blockStart_;
  }

  public void setBlockStart(TBlockStart node) {
    if (_blockStart_ != null) {
      _blockStart_.parent(null);
    }

    if (node != null) {
      if (node.parent() != null) {
        node.parent().removeChild(node);
      }

      node.parent(this);
    }

    _blockStart_ = node;
  }

  public PIdentifiers getIdentifiers() {
    return _identifiers_;
  }

  public void setIdentifiers(PIdentifiers node) {
    if (_identifiers_ != null) {
      _identifiers_.parent(null);
    }

    if (node != null) {
      if (node.parent() != null) {
        node.parent().removeChild(node);
      }

      node.parent(this);
    }

    _identifiers_ = node;
  }

  public TBlockEnd getBlockEnd() {
    return _blockEnd_;
  }

  public void setBlockEnd(TBlockEnd node) {
    if (_blockEnd_ != null) {
      _blockEnd_.parent(null);
    }

    if (node != null) {
      if (node.parent() != null) {
        node.parent().removeChild(node);
      }

      node.parent(this);
    }

    _blockEnd_ = node;
  }

  public String toString() {
    return "" + toString(_blockStart_) + toString(_identifiers_)
        + toString(_blockEnd_);
  }

  void removeChild(Node child) {
    if (_blockStart_ == child) {
      _blockStart_ = null;
      return;
    }

    if (_identifiers_ == child) {
      _identifiers_ = null;
      return;
    }

    if (_blockEnd_ == child) {
      _blockEnd_ = null;
      return;
    }

  }

  void replaceChild(Node oldChild, Node newChild) {
    if (_blockStart_ == oldChild) {
      setBlockStart((TBlockStart) newChild);
      return;
    }

    if (_identifiers_ == oldChild) {
      setIdentifiers((PIdentifiers) newChild);
      return;
    }

    if (_blockEnd_ == oldChild) {
      setBlockEnd((TBlockEnd) newChild);
      return;
    }

  }
}
