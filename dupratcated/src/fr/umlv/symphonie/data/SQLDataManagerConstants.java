
package fr.umlv.symphonie.data;

/**
 * This class contains all tables and fields that are used by the
 * <code>SQLDataManager</code> class.
 * @author susmab
 *
 */
public abstract class SQLDataManagerConstants {

  // tables names
  static public final String TABLE_STUDENT = "student";
  static public final String TABLE_COURSE = "course";
  static public final String TABLE_TITLE = "title";
  static public final String TABLE_HAS_MARK = "has_mark";
  static public final String TABLE_TEST = "test";
  static public final String TABLE_TIMESTAMP = "time_stamp";
  static public final String TABLE_TEACHER_FORMULA = "teacher_formula";
  static public final String TABLE_JURY_FORMULA = "jury_formula";

  // fields for student table
  static public final String COLUMN_ID_FROM_TABLE_STUDENT = "id_student";
  static public final String COLUMN_NAME_FROM_TABLE_STUDENT = "name";
  static public final String COLUMN_LAST_NAME_FROM_TABLE_STUDENT = "last_name";
  static public final String COLUMN_COMMENT_FROM_TABLE_STUDENT = "comment";

  // fields for course table
  static public final String COLUMN_ID_FROM_TABLE_COURSE = "id_course";
  static public final String COLUMN_TITLE_FROM_TABLE_COURSE = "title";
  static public final String COLUMN_COEFF_FROM_TABLE_COURSE = "coeff";

  // fields for title table
  static public final String COLUMN_ID_FROM_TABLE_TITLE = "id_title";
  static public final String COLUMN_DESC_FROM_TABLE_TITLE = "id_desc";

  // fields for has_mark table
  static public final String COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK = "id_student";
  static public final String COLUMN_ID_TEST_FROM_TABLE_HAS_MARK = "id_test";
  static public final String COLUMN_MARK_FROM_TABLE_HAS_MARK = "mark";

  // fields for test table
  static public final String COLUMN_ID_FROM_TABLE_TEST = "id_test";
  static public final String COLUMN_COEFF_FROM_TABLE_TEST = "coeff";
  static public final String COLUMN_ID_COURSE_FROM_TABLE_TEST = "id_course";
  static public final String COLUMN_ID_TITLE_FROM_TABLE_TEST = "id_title";

  // fields for time_stamp table
  static public final String COLUMN_TABLE_NAME_FROM_TABLE_TIMESTAMP = "table_name";
  static public final String COLUMN_TIMESTAMP_FROM_TABLE_TIMESTAMP = "stamp";
  
  // commun fields for the formulas tables
  static public final String COLUMN_ID_FORMULA_FROM_TABLE_FORMULA = "id_formula";
  static public final String COLUMN_ID_TITLE_FROM_TABLE_FORMULA = "id_title";
  static public final String COLUMN_EXPRESSION_FROM_TABLE_FORMULA = "expression";
  static public final String COLUMN_COLUMN_FROM_TABLE_FORMULA = "column_index";
  
  // specific field to teacher_formula table
  static public final String COLUMN_ID_COURSE_FROM_TABLE_TEACHER_FORMULA = "id_course";
}
