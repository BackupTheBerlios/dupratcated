package fr.umlv.symphonie.data;

public abstract class SQLDataManagerConstants
{
  // nom des tables
	static public final String TABLE_STUDENT = "student";
	static public final String TABLE_COURSE = "course";
	static public final String TABLE_TITLE = "title";
	static public final String TABLE_HAS_MARK = "has_mark";
  static public final String TABLE_TEST = "test";
  static public final String TABLE_TIMESTAMP = "timestamp";
  
  // attributs de la table student
	static public final String COLUMN_ID_FROM_TABLE_STUDENT = "id_student";
	static public final String COLUMN_NAME_FROM_TABLE_STUDENT = "name";
	static public final String COLUMN_LAST_NAME_FROM_TABLE_STUDENT = "last_name";
	static public final String COLUMN_COMMENT_FROM_TABLE_STUDENT = "comment";
  
  // attributs de la table course
	static public final String COLUMN_ID_FROM_TABLE_COURSE = "id_course";
	static public final String COLUMN_TITLE_FROM_TABLE_COURSE = "title";
	static public final String COLUMN_COEFF_FROM_TABLE_COURSE = "coeff";
  
  // attributs de la table title
	static public final String COLUMN_ID_FROM_TABLE_TITLE = "id_title";
	static public final String COLUMN_DESC_FROM_TABLE_TITLE = "id_desc";
  
  // attributs de la table has_mark
	static public final String COLUMN_ID_STUDENT_FROM_TABLE_HAS_MARK = "id_student";
	static public final String COLUMN_ID_TEST_FROM_TABLE_HAS_MARK = "id_test";
	static public final String COLUMN_MARK_FROM_TABLE_HAS_MARK = "mark";

  // attributs de la table test
  static public final String COLUMN_ID_FROM_TABLE_TEST = "id_test";
  static public final String COLUMN_COEFF_FROM_TABLE_TEST = "coeff";
  static public final String COLUMN_ID_COURSE_FROM_TABLE_TEST = "id_course";
  static public final String COLUMN_ID_TITLE_FROM_TABLE_TEST = "id_title";
  
  // attributs de la table timestamp
  static public final String COLUMN_TABLE_NAME_FROM_TABLE_TIMESTAMP = "table_name";
  static public final String COLUMN_TIMESTAMP_FROM_TABLE_TIMESTAMP = "stamp";
}