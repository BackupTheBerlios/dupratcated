/*
 * This file is part of Symphonie
 * Created : 22-févr.-2005 21:04:25
 */
package fr.umlv.symphonie.util;


/**
 * @author susmab
 *
 */
public class Pair<A, B> {

  private final A first;
  private final B second;
  
  
  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

  public A getFirst() {
    return first;
  }
  
  public B getSecond() {
    return second;
  }

}
