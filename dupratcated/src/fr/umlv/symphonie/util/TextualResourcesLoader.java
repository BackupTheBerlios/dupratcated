/*
 * This file is part of Symphonie
 * Created : 3 févr. 2005 17:50:16
 */

package fr.umlv.symphonie.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

/**
 * This class consists exclusively on a static methods used to load textual
 * resources from files. A textual resource file is an unicode Unix text file
 * whose lines contains pairs of <i>= </i>separated <code>String</code> s like
 * this :
 * <p>
 * Resource1=I do <br>
 * Resource2=love <br>
 * Resource3=java <br>
 * Resource4=so much
 * <p>
 * Left side expressions are key and right side expressions its corresponding
 * values.
 * 
 * @author fvallee, lgarcia, npersin & spenasal
 */
public class TextualResourcesLoader {

  public static final String DEFAULT_CHARSET = "unicode";

  /**
   * Default constructor
   */
  private TextualResourcesLoader() {
  }

  /**
   * Tries to load textual resources from a Locale-specific file using the given
   * charset <br>
   * File names are calculated from the file pattern and a suffix given by the
   * locale language ex :<br>
   * If filePattern is "/home/symphonie/text/resources" and Locale language is
   * "french", method will attempt to load data from file
   * "/home/symphonie/text/resources_french".
   * 
   * @param filePattern
   *          The file including some prefix or path.
   * @param l
   *          The locale used as suffix for the file
   * @param charset
   *          The encoding type used to convert bytes from the file into
   *          characters
   * @return A <code>HashMap</code> containing the data readen from the file
   * @throws FileNotFoundException
   *           If the file doesn't exist
   */
  public static HashMap<String, String> getResourceMap(String filePattern,
      Locale l, String charset) throws IOException {
    URL file = TextualResourcesLoader.class.getResource(filePattern + "_"
        + l.getDisplayLanguage());
    HashMap<String, String> m = new HashMap<String, String>();
    readFile(file.openStream(), charset, m);
    return m;
  }

  /**
   * Tries to load textual resources from a Locale-specific file using the
   * default charset (Unicode) <br>
   * 
   * @see #getResourceMap(String, Locale, String)
   * @param filePattern
   *          The file including some prefix or path.
   * @param l
   *          The locale used as suffix for the file
   * @return A <code>HashMap</code> containing the data readen from the file
   * @throws FileNotFoundException
   *           If the file doesn't exist
   */
  public static HashMap<String, String> getResourceMap(String filePattern,
      Locale l) throws IOException {
    return getResourceMap(filePattern, l, DEFAULT_CHARSET);
  }

  /**
   * Load contents from a file <br>
   * If file lines are not correctly formed you are likely to get a
   * <code>NullPointerException</code>
   * 
   * @param f
   *          The <code>FileInputStream</code> to read
   * @param resources
   *          The map to store contents read
   */
  private static void readFile(InputStream is, String charset,
      HashMap<String, String> resources) {
    Scanner lines = new Scanner(is, charset);
    lines.useDelimiter("(\n)|(\r\n)");
    String[] split;
    while (lines.hasNext()) {
      split = lines.next().split("=");
      resources.put(split[0], split[1]);
    }
  }
}
