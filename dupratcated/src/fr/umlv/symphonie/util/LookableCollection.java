/*
 * This file is part of Symphonie
 * Created on 24 mars 2005
 */

package fr.umlv.symphonie.util;

import java.util.Collection;

/**
 * Interface defines a collection where one can easily search for objects
 * 
 * @author spenasal
 */
public interface LookableCollection<T> extends Collection<T> {

  /**
   * Perform a lookup and returns the closest matching object to the passed one.
   * 
   * @param t
   *          The object to use as the base for the lookup.
   */
  public T lookup(T t);
}
