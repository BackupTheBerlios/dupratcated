/*
 * This file is part of Symphonie
 * Created : 22-févr.-2005 21:04:25
 */
package fr.umlv.symphonie.util;


/**
 * A simple class which represents a pair of Objects.
 * @author susmab
 *
 */
public class Pair<A, B> {

  /**
   * The first element of the pair.
   */
  private final A first;
  
  /**
   * The second element of the pair.
   */
  private final B second;
  
  
  /**
   * Constructs a pair for two given objects.
   * @param first the first element.
   * @param second the second element.
   */
  public Pair(A first, B second) {
    this.first = first;
    this.second = second;
  }

  /**
   * Used to get the first element.
   * @return the first element.
   */
  public A getFirst() {
    return first;
  }
  
  /**
   * Used to get the second element.
   * @return the second element.
   */
  public B getSecond() {
    return second;
  }
}
