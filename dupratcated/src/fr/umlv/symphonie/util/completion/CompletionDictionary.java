/*
 * This file is part of Symphonie
 * Created : 14 mars 2005 22:19:18
 */

package fr.umlv.symphonie.util.completion;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import fr.umlv.symphonie.util.LookableCollection;

/**
 * Convenient dictionary class for autocompletion that uses a
 * <code>TreeSet</code> to store data. All <code>Collection</code> methods
 * are delegated to the embedded <code>TreeSet</code>.<br>
 */
public class CompletionDictionary implements LookableCollection<String> {

  /** Internal data */
  private final TreeSet<String> words;

  /**
   * Default constructor. Creates a dictionary using a case-insensitive
   * comparator
   */
  public CompletionDictionary() {
    this(String.CASE_INSENSITIVE_ORDER);
  }

  /**
   * Creates a new dictionary that sorts words using the given separator
   * 
   * @param comp
   */
  public CompletionDictionary(Comparator<String> comp) {
    words = new TreeSet<String>(comp);
  }

  public int size() {
    return words.size();
  }

  public boolean isEmpty() {
    return words.isEmpty();
  }

  public boolean contains(Object o) {
    return words.contains(o);
  }

  public Iterator<String> iterator() {
    return words.iterator();
  }

  public Object[] toArray() {
    return words.toArray();
  }

  public <T> T[] toArray(T[] a) {
    return words.toArray(a);
  }

  public boolean add(String o) {
    return words.add(o);
  }

  public boolean remove(Object o) {
    return words.remove(o);
  }

  public boolean containsAll(Collection< ? > c) {
    return words.containsAll(c);
  }

  public boolean addAll(Collection< ? extends String> c) {
    return words.addAll(c);
  }

  public boolean removeAll(Collection< ? > c) {
    return words.removeAll(c);
  }

  public boolean retainAll(Collection< ? > c) {
    return words.retainAll(c);
  }

  public void clear() {
    words.clear();
  }

  // ----------------------------------------------------------------------------
  // Implement the LookableCollection interface methods
  // ----------------------------------------------------------------------------

  /**
   * Looks up the given string in the current dictionary, search is case
   * insensitive
   * 
   * @return <code>null</code> if <code>prefix</code> is null or there's no
   *         string that starts with <code>prefix</code>.<br>
   */
  public String lookup(String prefix) {
    if (prefix == null) return null;
    SortedSet<String> s = words.tailSet(prefix);
    String rs = s.isEmpty() ? null : s.first();
    boolean b = false;
    if (rs != null) b = rs.toLowerCase().startsWith(prefix.toLowerCase());
    return b ? rs : null;
  }
}
