/*
 * This file is part of Symphonie
 * Created : 10 mars 2005 14:21:44
 */

package fr.umlv.symphonie.view;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import fr.umlv.symphonie.util.ComponentBuilder;

/**
 * Class contains a static method that you can use to get the html code for the
 * welcome page.
 */
public final class WelcomePage {

  /** Welcome page file name */
  private static final String FILE_NAME = "readme.htm";

  /** Welcome page url */
  private static final URL WELCOME_PAGE = WelcomePage.class
      .getResource(FILE_NAME);

  /** Welcome page html text */
  private static String HTML_TEXT = null;

  /**
   * Returns the HTML renderable text using the given builder
   * 
   * @param b
   *          The builder for internationalization
   * @return a <code>String</code> containing HTML code
   * @throws IOException
   *           if there's an error reading default page
   */
  public static String getRenderableHTMLText(ComponentBuilder b)
      throws IOException {

    if (HTML_TEXT == null) {
      Scanner s = new Scanner(WELCOME_PAGE.openStream(), "iso-8859-1");
      s.useDelimiter("va-te-faire-mettre");
      HTML_TEXT = s.next();
      String path = WELCOME_PAGE.getProtocol() + "://" + WELCOME_PAGE.getPath();
      HTML_TEXT = HTML_TEXT.replaceFirst("base_url", path.substring(0, path
          .indexOf(FILE_NAME)));
    }

    String htmlText = HTML_TEXT.replaceFirst(TITLE, b.getValue(TITLE));
    htmlText = htmlText.replaceFirst(VIEWS_TITLE, b.getValue(VIEWS_TITLE));
    htmlText = htmlText.replaceFirst(STUDENT_VIEW, b.getValue(STUDENT_VIEW));
    htmlText = htmlText.replaceFirst(STUDENT_VIEW_DESC, b
        .getValue(STUDENT_VIEW_DESC));
    htmlText = htmlText.replaceFirst(TEACHER_VIEW, b.getValue(TEACHER_VIEW));
    htmlText = htmlText.replaceFirst(TEACHER_VIEW_DESC, b
        .getValue(TEACHER_VIEW_DESC));
    htmlText = htmlText.replaceFirst(JURY_VIEW, b.getValue(JURY_VIEW));
    htmlText = htmlText
        .replaceFirst(JURY_VIEW_DESC, b.getValue(JURY_VIEW_DESC));
    htmlText = htmlText.replaceFirst(END_MESSAGE, b.getValue(END_MESSAGE));
    return htmlText;
  }

  // ---------------------------------------------------------------------------
  // Static fields
  // ---------------------------------------------------------------------------

  public static final String TITLE = "html_title";
  public static final String VIEWS_TITLE = "html_views";
  public static final String STUDENT_VIEW = "html_student_view";
  public static final String STUDENT_VIEW_DESC = "html_student_view_desc";
  public static final String TEACHER_VIEW = "html_teacher_view";
  public static final String TEACHER_VIEW_DESC = "html_teacher_view_desc";
  public static final String JURY_VIEW = "html_jury_view";
  public static final String JURY_VIEW_DESC = "html_jury_view_desc";
  public static final String END_MESSAGE = "html_end_message";
}
