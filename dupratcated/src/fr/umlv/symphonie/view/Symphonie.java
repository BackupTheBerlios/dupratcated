/*
 * This file is part of Symphonie Created : 17 févr. 2005 21:30:59
 */

package fr.umlv.symphonie.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.model.CourseTreeModel;
import fr.umlv.symphonie.model.StudentModel;
import fr.umlv.symphonie.model.StudentTreeModel;
import fr.umlv.symphonie.model.TeacherModel;
import fr.umlv.symphonie.util.SymphonieComponentBuilder;
import fr.umlv.symphonie.util.TextualResourcesLoader;
import fr.umlv.symphonie.view.cells.CellRendererFactory;

public class Symphonie {

  private static final String FILE_PATTERN = "language/symphonie";
  private static final String FILE_CHARSET = "ISO-8859-1";

  private static SymphonieComponentBuilder builder;
  private static HashMap<String, String> english;
  private static HashMap<String, String> french;
  private static JFrame frame;

  private static JFrame getFrame() throws DataManagerException {

    try {
      english = TextualResourcesLoader.getResourceMap(FILE_PATTERN, new Locale(
          "english"), FILE_CHARSET);
      french = TextualResourcesLoader.getResourceMap(FILE_PATTERN, new Locale(
          "french"), FILE_CHARSET);
    } catch (IOException e) {
      throw new AssertionError("Bim ! -> " + e.getMessage());
    }

    builder = new SymphonieComponentBuilder(french);
    frame = new JFrame(builder.getValue(SymphonieConstants.FRAME_TITLE));
    frame.setContentPane(getContentPane(frame, builder));

    JMenuBar bar = new JMenuBar();
    bar.add(getFileMenu());
    bar.add(getWindowMenu());
    bar.add(getFormatMenu());
    bar.add(getInsertMenu());
    bar.add(getAdminMenu());

    frame.setJMenuBar(bar);

    return frame;

  }

  /**
   * Build "File" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getFileMenu() {
    JMenu file = builder.buildMenu(SymphonieConstants.FILE_MENU);

    /*
     * Actions Menu "File"
     * *******************************************************
     */

    Action a_import = SymphonieActionFactory.getImportAction(new ImageIcon(
        Symphonie.class.getResource("icons/import.png")));

    Action export = SymphonieActionFactory.getExportAction(new ImageIcon(
        Symphonie.class.getResource("icons/export.png")));

    Action print = SymphonieActionFactory.getPrintAction(new ImageIcon(
        Symphonie.class.getResource("icons/print.png")));

    Action exit = SymphonieActionFactory.getExitAction(new ImageIcon(
        Symphonie.class.getResource("icons/exit.png")));

    /*
     * Items Menu "File"
     * *********************************************************
     */

    file.add(builder.buildMenuItem(a_import,
        SymphonieConstants.IMPORT_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    file.add(builder.buildMenuItem(export, SymphonieConstants.EXPORT_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));

    file.add(new JSeparator());

    file.add(builder.buildMenuItem(print, SymphonieConstants.PRINT_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));

    file.add(new JSeparator());

    file.add(builder.buildMenuItem(exit, SymphonieConstants.EXIT_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));

    return file;
  }

  /**
   * Build "Language" JMenu included in the "Window" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getLangMenu() {
    JMenu lang = builder.buildMenu(SymphonieConstants.LANGUAGE_MENU_ITEM);

    // Actions
    Action inEnglish = SymphonieActionFactory.getLanguageChangeAction(english,
        builder);
    Action inFrench = SymphonieActionFactory.getLanguageChangeAction(french,
        builder);

    // Items

    JCheckBoxMenuItem frBox = (JCheckBoxMenuItem) builder.buildMenuItem(
        inFrench, SymphonieConstants.FRENCH_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.CHECK_BOX_ITEM);
    frBox.setSelected(true);

    JCheckBoxMenuItem engBox = (JCheckBoxMenuItem) builder.buildMenuItem(
        inEnglish, SymphonieConstants.ENGLISH_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.CHECK_BOX_ITEM);

    ButtonGroup g = new ButtonGroup();
    g.add(engBox);
    g.add(frBox);

    lang.add(engBox);
    lang.add(frBox);

    lang.setIcon(new ImageIcon(Symphonie.class.getResource("icons/lang.png")));

    return lang;
  }

  /**
   * Build "Mode" JMenu included in the "Window" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getModeMenu() {
    JMenu mode = builder.buildMenu(SymphonieConstants.CHANGE_VIEW_MENU);
    // Actions
    Action viewStudent = SymphonieActionFactory.getModeChangeAction("student",
        new ImageIcon());
    Action viewJury = SymphonieActionFactory.getModeChangeAction("jury",
        new ImageIcon());
    Action viewTeacher = SymphonieActionFactory.getModeChangeAction("teacher",
        new ImageIcon());

    // Items
    JCheckBoxMenuItem studentBox = (JCheckBoxMenuItem) builder.buildMenuItem(
        viewStudent, SymphonieConstants.VIEW_STUDENT_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.CHECK_BOX_ITEM);

    JCheckBoxMenuItem juryBox = (JCheckBoxMenuItem) builder.buildMenuItem(
        viewJury, SymphonieConstants.VIEW_JURY_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.CHECK_BOX_ITEM);

    JCheckBoxMenuItem teacherBox = (JCheckBoxMenuItem) builder.buildMenuItem(
        viewTeacher, SymphonieConstants.VIEW_TEACHER_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.CHECK_BOX_ITEM);

    ButtonGroup g = new ButtonGroup();
    g.add(studentBox);
    g.add(juryBox);
    g.add(teacherBox);

    mode.add(studentBox);
    mode.add(juryBox);
    mode.add(teacherBox);

    return mode;
  }

  /**
   * Build "Window" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getWindowMenu() {
    JMenu window = builder.buildMenu(SymphonieConstants.WINDOW_MENU);

    /* Items********************************************************* */
    window.add(getModeMenu());
    window.add(getLangMenu());

    return window;

  }

  /**
   * Build "Format" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getFormatMenu() {
    JMenu format = builder.buildMenu(SymphonieConstants.FORMAT_MENU);

    /* Actions ******************************************************* */
    Action formula = SymphonieActionFactory.getFormulaAction(new ImageIcon(
        Symphonie.class.getResource("icons/formula.png")), frame, builder);
    Action f_cell = SymphonieActionFactory.getFormulaCellAction(
        new ImageIcon(), frame, builder);

    /* Items********************************************************* */
    format.add(builder.buildMenuItem(formula,
        SymphonieConstants.FORMULA_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    format.add(builder.buildMenuItem(f_cell, SymphonieConstants.CELL_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));

    return format;
  }

  /**
   * Build "Insert" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getInsertMenu() {
    JMenu insert = builder.buildMenu(SymphonieConstants.INSERT_MENU);
    /* Actions ******************************************************* */
    Action column = SymphonieActionFactory.getColumnAction(new ImageIcon(
        Symphonie.class.getResource("icons/insert_column.png")));
    Action line = SymphonieActionFactory.getLineAction(new ImageIcon(
        Symphonie.class.getResource("icons/insert_line.png")));

    /* Items********************************************************* */
    insert.add(builder.buildMenuItem(column,
        SymphonieConstants.INSERT_COLUMN_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    insert.add(builder.buildMenuItem(line,
        SymphonieConstants.INSERT_LINE_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    return insert;
  }

  /**
   * Build "Admin" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getAdminMenu() {
    JMenu admin = builder.buildMenu(SymphonieConstants.ADMIN_MENU);
    /* Actions ******************************************************* */
    Action connect = SymphonieActionFactory.getConnectAction(new ImageIcon(
        Symphonie.class.getResource("icons/admin.png")), builder);
    Action db = SymphonieActionFactory.getDBAction(new ImageIcon(
        Symphonie.class.getResource("icons/db.png")));
    Action pwd = SymphonieActionFactory.getPwdAction(new ImageIcon(
        Symphonie.class.getResource("icons/pwd.png")));

    /* Items********************************************************* */
    admin.add(builder.buildMenuItem(connect,
        SymphonieConstants.CONNECT_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    admin.add(builder.buildMenuItem(db, SymphonieConstants.DB_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    admin.add(builder.buildMenuItem(pwd, SymphonieConstants.PWD_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    return admin;
  }

  /**
   * Build the toolbar
   * 
   * @return JToolBar
   */
  private static JToolBar getToolbar() {
    JToolBar toolbar = new JToolBar();
    toolbar.add(SymphonieActionFactory.getConnectAction(new ImageIcon(
        Symphonie.class.getResource("icons/connect.png")), builder));
    toolbar.add(SymphonieActionFactory.getDBAction(new ImageIcon(
        Symphonie.class.getResource("icons/db.png"))));

    toolbar.setFloatable(false);

    return toolbar;
  }

  private static JSplitPane getTeacherPane(DataManager dataManager) {
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    /* table */

    final TeacherModel teacherModel = new TeacherModel(dataManager);

    final JTable table = new JTable(teacherModel);
    table.setTableHeader(null);

    table.setDefaultRenderer(Object.class, CellRendererFactory
        .getTeacherModelCellRenderer(teacherModel.getFormattedObjects()));

    JScrollPane scroll1 = new JScrollPane(table);

    split.setRightComponent(scroll1);

    /*
     * arbre
     */
    CourseTreeModel courseModel = new CourseTreeModel(dataManager);

    final JTree tree = new JTree(courseModel);

    tree.setCellRenderer(new DefaultTreeCellRenderer() {

      private final Icon leafIcon = new ImageIcon(StudentTreeModel.class
          .getResource("../view/icons/course.png"));
      private final Icon rootIcon = new ImageIcon(StudentTreeModel.class
          .getResource("../view/icons/courses.png"));

      public java.awt.Component getTreeCellRendererComponent(JTree tree,
          Object value, boolean sel, boolean expanded, boolean leaf, int row,
          boolean hasFocus) {

        Font font = getFont();

        if (font != null) {
          font = font.deriveFont(Font.BOLD);
          setFont(font);
        }

        if (leaf) {
          setLeafIcon(leafIcon);
        } else {
          setClosedIcon(rootIcon);
          setOpenIcon(rootIcon);
        }

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
            row, hasFocus);
        return this;
      }
    });

    tree.addTreeSelectionListener(new TreeSelectionListener() {

      /*
       * (non-Javadoc)
       * 
       * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
       */
      public void valueChanged(TreeSelectionEvent e) {
        Object o = tree.getLastSelectedPathComponent();

        if (o instanceof Course) {
          System.out.println("on a selectionne une matiere !");
          ((TeacherModel) table.getModel()).setCourse((Course) o);
        }
      }

    });

    JScrollPane pane = new JScrollPane(tree);

    split.setLeftComponent(pane);

    return split;

  }

  private static JSplitPane getStudentPane(DataManager dataManager)
      throws DataManagerException {
    StudentModel studentModel = new StudentModel(dataManager);

    Map<Integer, Student> studentMap = null;

    studentMap = dataManager.getStudents();

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    /*
     * table
     */

    final JTable table = new JTable(studentModel);
    table.setTableHeader(null);

    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

      public java.awt.Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus, int row,
          int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);

        label.setHorizontalAlignment(SwingConstants.CENTER);

        if (column == 0 || row % 4 == 0)
          label.setFont(getFont().deriveFont(Font.BOLD));

        return label;
      }
    });

    JScrollPane scroll1 = new JScrollPane(table);
    split.setRightComponent(scroll1);

    /*
     * arbre
     */

    StudentTreeModel treeModel = new StudentTreeModel(dataManager);
    final JTree tree = new JTree(treeModel);

    tree.setCellRenderer(new DefaultTreeCellRenderer() {

      private final Icon leafIcon = new ImageIcon(StudentTreeModel.class
          .getResource("../view/icons/student.png"));
      private final Icon rootIcon = new ImageIcon(StudentTreeModel.class
          .getResource("../view/icons/students.png"));

      public java.awt.Component getTreeCellRendererComponent(JTree tree,
          Object value, boolean sel, boolean expanded, boolean leaf, int row,
          boolean hasFocus) {

        Font font = getFont();

        if (font != null) {
          font = font.deriveFont(Font.BOLD);
          setFont(font);
        }

        if (leaf) {
          setLeafIcon(leafIcon);
        } else {
          setClosedIcon(rootIcon);
          setOpenIcon(rootIcon);
        }

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
            row, hasFocus);
        return this;
      }
    });

    JScrollPane pane = new JScrollPane(tree);

    split.setLeftComponent(pane);

    tree.addTreeSelectionListener(new TreeSelectionListener() {

      /*
       * (non-Javadoc)
       * 
       * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
       */
      public void valueChanged(TreeSelectionEvent e) {
        Object o = tree.getLastSelectedPathComponent();

        if (o instanceof Student)
          ((StudentModel) (table.getModel())).setStudent((Student) o);

      }
    });

    return split;
  }

  private static JPanel getContentPane(final JFrame f,
      final SymphonieComponentBuilder builder) throws DataManagerException {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(getToolbar(), BorderLayout.NORTH);

    DataManager dataManager = new SQLDataManager();

    
    JTabbedPane tab = new JTabbedPane(JTabbedPane.BOTTOM,
        JTabbedPane.SCROLL_TAB_LAYOUT);
    
    /* Student JTabbedPane */
    tab.add(SymphonieConstants.VIEW_STUDENT_MENU_ITEM, new JScrollPane(getStudentPane(dataManager)));
    panel.add(tab);
    
    /* Teacher JTabbedPane */

    tab.add(SymphonieConstants.VIEW_TEACHER_MENU_ITEM, new JScrollPane(getTeacherPane(dataManager)));
    panel.add(tab);
    
    builder.addChangeListener(panel, new ChangeListener() {

      public void stateChanged(ChangeEvent arg0) {
        f.setTitle(builder.getValue(SymphonieConstants.FRAME_TITLE));
      }
    });

    return panel;
  }

  public static void main(String[] args) throws DataManagerException {
    JFrame f = getFrame();
    f.pack();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
  }
}
