/*
 * This file is part of Symphonie Created : 17 f�vr. 2005 21:30:59
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
import javax.swing.JComponent;
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
import fr.umlv.symphonie.model.JuryModel;
import fr.umlv.symphonie.model.StudentModel;
import fr.umlv.symphonie.model.StudentTreeModel;
import fr.umlv.symphonie.model.TeacherModel;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.TextualResourcesLoader;
import fr.umlv.symphonie.view.cells.CellRendererFactory;

public class Symphonie {

  private static final String FILE_PATTERN = "language/symphonie";
  private static final String FILE_CHARSET = "ISO-8859-1";

  private static ComponentBuilder builder;
  private static HashMap<String, String> english;
  private static HashMap<String, String> french;
  private static HashMap<String, String> spanish;
  private static JFrame frame;
  private static JTabbedPane tab;

  private static JFrame getFrame() throws DataManagerException {

    try {
      english = TextualResourcesLoader.getResourceMap(FILE_PATTERN, new Locale(
          "english"), FILE_CHARSET);
      french = TextualResourcesLoader.getResourceMap(FILE_PATTERN, new Locale(
          "french"), FILE_CHARSET);
      spanish = TextualResourcesLoader.getResourceMap(FILE_PATTERN, new Locale(
      "spanish"), FILE_CHARSET);
    } catch (IOException e) {
      throw new AssertionError("Bim ! -> " + e.getMessage());
    }

    builder = new ComponentBuilder(french);
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
    JMenu file = (JMenu) builder.buildButton(SymphonieConstants.FILE_MENU, ComponentBuilder.ButtonType.MENU);

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

    file.add(builder.buildButton(a_import,
        SymphonieConstants.IMPORT_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));
    file.add(builder.buildButton(export, SymphonieConstants.EXPORT_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));

    file.add(new JSeparator());

    file.add(builder.buildButton(print, SymphonieConstants.PRINT_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));

    file.add(new JSeparator());

    file.add(builder.buildButton(exit, SymphonieConstants.EXIT_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));

    return file;
  }

  /**
   * Build the nested JMenu "Language" included in the "Window" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getLangMenu() {
    JMenu lang = (JMenu) builder.buildButton(SymphonieConstants.LANGUAGE_MENU_ITEM, ComponentBuilder.ButtonType.MENU);

    // Actions
    Action inEnglish = SymphonieActionFactory.getLanguageChangeAction(english,
        builder);
    Action inFrench = SymphonieActionFactory.getLanguageChangeAction(french,
        builder);
    Action inSpanish = SymphonieActionFactory.getLanguageChangeAction(spanish,
        builder);

    // Items

    JCheckBoxMenuItem frBox = (JCheckBoxMenuItem) builder.buildButton(
        inFrench, SymphonieConstants.FRENCH_MENU_ITEM,
        ComponentBuilder.ButtonType.CHECK_BOX_MENU_ITEM);
    frBox.setSelected(true);

    JCheckBoxMenuItem engBox = (JCheckBoxMenuItem) builder.buildButton(
        inEnglish, SymphonieConstants.ENGLISH_MENU_ITEM,
        ComponentBuilder.ButtonType.CHECK_BOX_MENU_ITEM);
    
    JCheckBoxMenuItem spBox = (JCheckBoxMenuItem) builder.buildButton(
        inSpanish, SymphonieConstants.SPANISH_MENU_ITEM,
        ComponentBuilder.ButtonType.CHECK_BOX_MENU_ITEM);

    ButtonGroup g = new ButtonGroup();
    g.add(engBox);
    g.add(frBox);
    g.add(spBox);

    lang.add(engBox);
    lang.add(frBox);
    lang.add(spBox);

    lang.setIcon(new ImageIcon(Symphonie.class.getResource("icons/lang.png")));

    return lang;
  }

  /**
   * Build the nested JMenu "Mode" included in the "Window" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getModeMenu() {
    JMenu mode = (JMenu) builder.buildButton(
        SymphonieConstants.CHANGE_VIEW_MENU, ComponentBuilder.ButtonType.MENU);
    // Actions
    Action viewStudent = SymphonieActionFactory.getModeChangeAction("student",
        new ImageIcon(), tab, 0);
    Action viewTeacher = SymphonieActionFactory.getModeChangeAction("teacher",
        new ImageIcon(), tab, 1);
    Action viewJury = SymphonieActionFactory.getModeChangeAction("jury",
        new ImageIcon(), tab, 2);

    // Items
    JCheckBoxMenuItem studentBox = (JCheckBoxMenuItem) builder.buildButton(
        viewStudent, SymphonieConstants.VIEW_STUDENT_MENU_ITEM,
        ComponentBuilder.ButtonType.CHECK_BOX_MENU_ITEM);

    JCheckBoxMenuItem teacherBox = (JCheckBoxMenuItem) builder.buildButton(
        viewTeacher, SymphonieConstants.VIEW_TEACHER_MENU_ITEM,
        ComponentBuilder.ButtonType.CHECK_BOX_MENU_ITEM);

    JCheckBoxMenuItem juryBox = (JCheckBoxMenuItem) builder.buildButton(
        viewJury, SymphonieConstants.VIEW_JURY_MENU_ITEM,
        ComponentBuilder.ButtonType.CHECK_BOX_MENU_ITEM);

    ButtonGroup g = new ButtonGroup();
    g.add(studentBox);
    g.add(teacherBox);
    g.add(juryBox);

    mode.add(studentBox);
    mode.add(teacherBox);
    mode.add(juryBox);

    return mode;
  }

  /**
   * Build "Window" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getWindowMenu() {
    JMenu window = (JMenu) builder.buildButton(SymphonieConstants.WINDOW_MENU,
        ComponentBuilder.ButtonType.MENU);

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
    JMenu format = (JMenu) builder.buildButton(SymphonieConstants.FORMAT_MENU,
        ComponentBuilder.ButtonType.MENU);

    /* Actions ******************************************************* */
    Action formula = SymphonieActionFactory.getFormulaAction(new ImageIcon(
        Symphonie.class.getResource("icons/formula.png")), frame, builder);
    Action f_cell = SymphonieActionFactory.getFormulaCellAction(
        new ImageIcon(), frame, builder);

    /* Items********************************************************* */
    format.add(builder.buildButton(formula,
        SymphonieConstants.FORMULA_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));
    format.add(builder.buildButton(f_cell, SymphonieConstants.CELL_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));

    return format;
  }

  /**
   * Build "Insert" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getInsertMenu() {
    JMenu insert = (JMenu) builder.buildButton(SymphonieConstants.INSERT_MENU,
        ComponentBuilder.ButtonType.MENU);
    /* Actions ******************************************************* */
    Action column = SymphonieActionFactory.getColumnAction(new ImageIcon(
        Symphonie.class.getResource("icons/insert_column.png")));
    Action line = SymphonieActionFactory.getLineAction(new ImageIcon(
        Symphonie.class.getResource("icons/insert_line.png")));

    /* Items********************************************************* */
    insert.add(builder.buildButton(column,
        SymphonieConstants.INSERT_COLUMN_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));
    insert.add(builder.buildButton(line,
        SymphonieConstants.INSERT_LINE_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));
    return insert;
  }

  /**
   * Build "Admin" JMenu
   * 
   * @return JMenu
   */
  private static JMenu getAdminMenu() {
    JMenu admin = (JMenu) builder.buildButton(SymphonieConstants.ADMIN_MENU,
        ComponentBuilder.ButtonType.MENU);
    /* Actions ******************************************************* */
    Action connect = SymphonieActionFactory.getConnectAction(new ImageIcon(
        Symphonie.class.getResource("icons/admin.png")), builder);
    Action db = SymphonieActionFactory.getDBAction(new ImageIcon(
        Symphonie.class.getResource("icons/db.png")), frame, builder);
    Action pwd = SymphonieActionFactory.getPwdAction(new ImageIcon(
        Symphonie.class.getResource("icons/pwd.png")), frame, builder);

    /* Items********************************************************* */
    admin.add(builder.buildButton(connect,
        SymphonieConstants.CONNECT_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));
    admin.add(builder.buildButton(db, SymphonieConstants.DB_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));
    admin.add(builder.buildButton(pwd, SymphonieConstants.PWD_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));
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
        Symphonie.class.getResource("icons/db.png")), frame, builder));

    toolbar.setFloatable(false);

    return toolbar;
  }

  /**
   * Build a JSplitPane with the TeacherModel
   * 
   * @param dataManager
   * @return JSplitPane
   */
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

  /**
   * @param dataManager
   * @return
   * @throws DataManagerException
   */
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

  /**
   * @param dataManager
   * @return JTable
   */
  private static JTable getJuryPane(DataManager dataManager) {
    JuryModel model = new JuryModel(dataManager);

    JTable table = new JTable(model);
    table.setTableHeader(null);

    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

      public java.awt.Component getTableCellRendererComponent(JTable table,
          Object value, boolean isSelected, boolean hasFocus, int row,
          int column) {
        JLabel label = (JLabel) super.getTableCellRendererComponent(table,
            value, isSelected, hasFocus, row, column);

        label.setHorizontalAlignment(SwingConstants.CENTER);

        if (column == 0 || row == 0
            || column == table.getModel().getColumnCount() - 2)
          label.setFont(getFont().deriveFont(Font.BOLD));

        return label;
      }
    });

    return table;
  }

  /**
   * @param f
   *          the frame
   * @param builder
   * @return JPanel
   * @throws DataManagerException
   */
  private static JPanel getContentPane(final JFrame f,
      final ComponentBuilder builder) throws DataManagerException {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(getToolbar(), BorderLayout.NORTH);

    DataManager dataManager = new SQLDataManager();

    tab = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);

    /* Student JTabbedPane */
    tab.add(builder.getValue(SymphonieConstants.VIEW_STUDENT_MENU_ITEM),
        new JScrollPane(getStudentPane(dataManager)));
    final int sTab = tab.indexOfTab(builder
        .getValue(SymphonieConstants.VIEW_STUDENT_MENU_ITEM));
    builder.addChangeListener((JComponent) tab.getComponentAt(sTab),
        new ChangeListener() {

          public void stateChanged(ChangeEvent e) {
            tab.setTitleAt(sTab, builder
                .getValue(SymphonieConstants.VIEW_STUDENT_MENU_ITEM));
          }
        });

    /* Teacher JTabbedPane */
    tab.add(builder.getValue(SymphonieConstants.VIEW_TEACHER_MENU_ITEM),
        new JScrollPane(getTeacherPane(dataManager)));
    final int tTab = tab.indexOfTab(builder
        .getValue(SymphonieConstants.VIEW_TEACHER_MENU_ITEM));
    builder.addChangeListener((JComponent) tab.getComponentAt(tTab),
        new ChangeListener() {

          public void stateChanged(ChangeEvent e) {
            tab.setTitleAt(tTab, builder
                .getValue(SymphonieConstants.VIEW_TEACHER_MENU_ITEM));
          }
        });

    /* Jury JTabbedPane */
    tab.add(builder.getValue(SymphonieConstants.VIEW_JURY_MENU_ITEM),
        new JScrollPane(getJuryPane(dataManager)));
    final int jTab = tab.indexOfTab(builder
        .getValue(SymphonieConstants.VIEW_JURY_MENU_ITEM));
    builder.addChangeListener((JComponent) tab.getComponentAt(jTab),
        new ChangeListener() {

          public void stateChanged(ChangeEvent e) {
            tab.setTitleAt(jTab, builder
                .getValue(SymphonieConstants.VIEW_JURY_MENU_ITEM));
          }
        });

    panel.add(tab);

    /* change the title of the frame */
    builder.addChangeListener(panel, new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
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
