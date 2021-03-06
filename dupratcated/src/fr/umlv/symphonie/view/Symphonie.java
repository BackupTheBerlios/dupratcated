/*
 * This file is part of Symphonie
 * Created : 17 f�vr. 2005 21:30:59
 */

package fr.umlv.symphonie.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SingleSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.model.AbstractSymphonieTableModel;
import fr.umlv.symphonie.model.AdminJuryModel;
import fr.umlv.symphonie.model.AdminStudentModel;
import fr.umlv.symphonie.model.AdminTeacherModel;
import fr.umlv.symphonie.model.CourseTreeModel;
import fr.umlv.symphonie.model.JuryModel;
import fr.umlv.symphonie.model.StudentModel;
import fr.umlv.symphonie.model.StudentTreeModel;
import fr.umlv.symphonie.model.TeacherModel;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ExceptionDisplayDialog;
import fr.umlv.symphonie.util.Pair;
import fr.umlv.symphonie.util.SymphoniePreferencesManager;
import fr.umlv.symphonie.util.TextualResourcesLoader;
import fr.umlv.symphonie.util.ComponentBuilder.ButtonType;
import fr.umlv.symphonie.util.dataexport.DataExporter;
import fr.umlv.symphonie.util.dataexport.DataExporterException;
import fr.umlv.symphonie.util.dataimport.DataImporter;
import fr.umlv.symphonie.util.dataimport.DataImporterException;
import fr.umlv.symphonie.util.identification.IdentificationException;
import fr.umlv.symphonie.util.identification.IdentificationStrategy;
import fr.umlv.symphonie.util.identification.SQLIdentificationStrategy;
import fr.umlv.symphonie.util.wizard.DefaultWizardModel;
import fr.umlv.symphonie.util.wizard.Wizard;
import static fr.umlv.symphonie.view.SymphonieConstants.ADDCOURSE;
import static fr.umlv.symphonie.view.SymphonieConstants.ADDMARKDIALOG_TITLE;
import static fr.umlv.symphonie.view.SymphonieConstants.ADD_FORMULA;
import static fr.umlv.symphonie.view.SymphonieConstants.ADD_STUDENT_TITLE;
import static fr.umlv.symphonie.view.SymphonieConstants.ADMIN_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.CELL_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.CHANGE_VIEW_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.CONNECT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.DB_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.DISPLAY_CHART;
import static fr.umlv.symphonie.view.SymphonieConstants.EXIT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.EXPORT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.FILE_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.FORMULA_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.FRAME_TITLE;
import static fr.umlv.symphonie.view.SymphonieConstants.IMPORT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.INSERT_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.JURYVIEWPOPUP_TITLE;
import static fr.umlv.symphonie.view.SymphonieConstants.LANGUAGE_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.LOGOUT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.PRINT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.PWD_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.REMOVECOURSE;
import static fr.umlv.symphonie.view.SymphonieConstants.REMOVE_CELL_FORMAT;
import static fr.umlv.symphonie.view.SymphonieConstants.REMOVE_COLUMN;
import static fr.umlv.symphonie.view.SymphonieConstants.REMOVE_STUDENT;
import static fr.umlv.symphonie.view.SymphonieConstants.SETSTEP;
import static fr.umlv.symphonie.view.SymphonieConstants.STUDENTVIEWPOPUP_TITLE;
import static fr.umlv.symphonie.view.SymphonieConstants.TEACHERVIEWPOPUP_TITLE;
import static fr.umlv.symphonie.view.SymphonieConstants.UPDATE;
import static fr.umlv.symphonie.view.SymphonieConstants.VIEW_JURY_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.VIEW_STUDENT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.VIEW_TEACHER_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.WINDOW_MENU;

public class Symphonie {

  /** Symphonie data source */
  private final DataManager manager;

  /** The international builder */
  protected ComponentBuilder builder;

  /** The default language */
  private Language currentLanguage = SymphoniePreferencesManager.getLanguage();

  /** The main frame */
  protected JFrame frame;

  /** The views tabbed pane */
  protected JTabbedPane tab = new JTabbedPane(JTabbedPane.BOTTOM,
      JTabbedPane.SCROLL_TAB_LAYOUT);

  /** Exception display tool */
  public final ExceptionDisplayDialog errDisplay;

  /** Current view */
  protected View currentView;

  /** Export wizard model */
  protected final Wizard exportW;

  /** Import wizard model */
  private final Wizard importW;

  /** Identification service */
  protected IdentificationStrategy logger;

  /** Student Table Model * */
  protected final StudentModel studentModel;

  /** Admin Student Table Model * */
  protected final AdminStudentModel adminStudentModel;

  /** Current Student Table Model */
  protected StudentModel currentStudentModel;

  /** Teacher Table Model * */
  protected final TeacherModel teacherModel;

  /** Admin Teacher Table Model * */
  protected final AdminTeacherModel adminTeacherModel;

  /** Current Teacher Table Model */
  protected TeacherModel currentTeacherModel;

  /** Jury Table Model * */
  protected final JuryModel juryModel;

  /** Admin Jury Table Model * */
  protected final AdminJuryModel adminJuryModel;

  /** Current Jury Table Model */
  protected JuryModel currentJuryModel;

  /** StudentTreeModel * */
  private final StudentTreeModel studentTreeModel;

  /** CourseTreeModel * */
  private final CourseTreeModel courseTreeModel;

  /** Action Factory * */
  protected final SymphonieActionFactory actionFactory;

  // ----------------------------------------------------------------------------
  // Menu building methods
  // ----------------------------------------------------------------------------

  /**
   * Builds the "File" frame menu
   * 
   * @param exp
   *          The export menu item
   * @param imp
   *          The import menu item
   * @param print
   *          The print menu item
   * @param exit
   *          The exit menu item
   * @return a <code>JMenu</code>
   */
  private final JMenu getFileMenu(JMenuItem exp, JMenuItem imp,
      JMenuItem print, JMenuItem exit) {
    JMenu file = (JMenu) builder.buildButton(FILE_MENU, ButtonType.MENU);

    file.add(print);
    file.add(new JSeparator());

    file.add(imp);
    file.add(exp);
    file.add(new JSeparator());

    file.add(exit);
    return file;
  }

  /**
   * Builds the nested "Language" menu included in the "Window" menu
   * 
   * @return a <code>JMenu</code>
   */
  private final JMenu getLangMenu() {
    JMenu lang = (JMenu) builder.buildButton(LANGUAGE_MENU_ITEM,
        ButtonType.MENU);
    ButtonGroup g = new ButtonGroup();
    JCheckBoxMenuItem lBox;
    HashMap<String, String> lMap;
    for (Language l : Language.values()) {
      try {
        lMap = l.getMap();
      } catch (IOException e) {
        System.err.println("Unable to load language : " + l.name()
            + " Skipping");
        continue;
      }
      lBox = new JCheckBoxMenuItem(actionFactory.getLanguageChangeAction(l));
      lBox.setText(lMap.get(Language.LANGUAGE_NAME));
      lang.add(lBox);
      g.add(lBox);
      if (currentLanguage.equals(l)) lBox.setSelected(true);
    }

    lang.setIcon(new ImageIcon(Symphonie.class.getResource("icons/lang.png")));
    return lang;
  }

  /**
   * Builds the nested "Mode" menu included in the "Window" menu
   * 
   * @return a <code>JMenu</code>
   */
  private final JMenu getModeMenu() {
    JMenu mode = (JMenu) builder.buildButton(CHANGE_VIEW_MENU, ButtonType.MENU);
    mode.setIcon(EMPTY_ICON);

    ButtonGroup g = new ButtonGroup();

    javax.swing.AbstractAction a;
    for (final View v : View.values()) {
      a = actionFactory.getModeChangeAction(v);
      final JCheckBoxMenuItem vBox = (JCheckBoxMenuItem) builder.buildButton(a,
          v.getNameKey(), ButtonType.CHECK_BOX_MENU_ITEM);
      g.add(vBox);
      mode.add(vBox);
      tab.getModel().addChangeListener(new ChangeListener() {

        public void stateChanged(ChangeEvent e) {
          SingleSelectionModel ssm = (SingleSelectionModel) e.getSource();
          int idx = ssm.getSelectedIndex();
          vBox.setSelected(idx == v.ordinal());
        }
      });
    }
    mode.setEnabled(false);
    return mode;
  }

  /**
   * Builds the "Window" menu
   * 
   * @return a <code>JMenu</code>
   */
  private final JMenu getWindowMenu(JMenu mode, JMenu lang) {
    JMenu window = (JMenu) builder.buildButton(WINDOW_MENU, ButtonType.MENU);
    window.add(mode);
    window.add(lang);
    return window;
  }

  /**
   * Builds the "Insert" menu
   * 
   * @param formula
   *          The add formula menu item
   * @param tbar
   *          A toolbar to add some stuff
   * @return a <code>JMenu</code>
   */
  private final JMenu getInsertMenu(JMenuItem formula, JToolBar tbar) {
    JMenu insert = (JMenu) builder.buildButton(INSERT_MENU, ButtonType.MENU);

    JMenuItem addCol = (JMenuItem) builder.buildButton(addColumn,
        ADDMARKDIALOG_TITLE, ButtonType.MENU_ITEM);

    insert.add(addCol);
    tbar.add(createToolbarButton(ADDMARKDIALOG_TITLE, addCol, builder));
    insert.add(formula);
    return insert;
  }

  /**
   * Builds the "Admin" menu
   * 
   * @param toolbar
   *          A toolbar to add some buttons
   * @return a <code>JMenu</code>
   */
  private final JMenu getAdminMenu(JToolBar toolbar) {

    JMenu admin = (JMenu) builder.buildButton(ADMIN_MENU, ButtonType.MENU);

    /* Actions ******************************************************* */
    final Action connect = actionFactory.getConnectAction(new ImageIcon(
        Symphonie.class.getResource("icons/admin.png")), logger);
    final Action logout = actionFactory.getLogoutAction(new ImageIcon(
        Symphonie.class.getResource("icons/logout.png")));
    logout.setEnabled(false);
    final Action db = actionFactory.getDBAction(new ImageIcon(Symphonie.class
        .getResource("icons/db.png")));
    db.setEnabled(false);
    final Action pwd = actionFactory.getPwdAction(new ImageIcon(Symphonie.class
        .getResource("icons/pwd.png")));
    pwd.setEnabled(false);

    logger.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        boolean b = logger.isIdentified();
        connect.setEnabled(!b);
        logout.setEnabled(b);
        db.setEnabled(b);
        pwd.setEnabled(b);
      }
    });

    /* Items********************************************************* */
    AbstractButton b = builder.buildButton(connect, CONNECT_MENU_ITEM,
        ButtonType.MENU_ITEM);
    admin.add(b);
    toolbar.add(createToolbarButton(CONNECT_MENU_ITEM, b, builder));

    b = builder.buildButton(logout, LOGOUT_MENU_ITEM, ButtonType.MENU_ITEM);
    admin.add(b);
    toolbar.add(createToolbarButton(LOGOUT_MENU_ITEM, b, builder));

    b = builder.buildButton(db, DB_MENU_ITEM, ButtonType.MENU_ITEM);
    admin.add(b);
    toolbar.add(createToolbarButton(DB_MENU_ITEM, b, builder));

    b = builder.buildButton(pwd, PWD_MENU_ITEM, ButtonType.MENU_ITEM);
    admin.add(b);
    toolbar.add(createToolbarButton(PWD_MENU_ITEM, b, builder));
    toolbar.addSeparator();

    return admin;
  }

  /**
   * Builds a ready-to-use menu bar
   * 
   * @param menus
   *          The menus that will be contained on the menu bar
   * @return a <code>JMenuBar</code>
   */
  private final JMenuBar getMenubar(JMenu... menus) {
    JMenuBar bar = new JMenuBar();
    for (JMenu m : menus)
      bar.add(m);
    return bar;
  }

  // ----------------------------------------------------------------------------
  // Wizards set up
  // ----------------------------------------------------------------------------

  /**
   * Provides an export wizard
   * 
   * @return The created wizard
   */
  private final Wizard getExportWizard() {
    DefaultWizardModel exp = (DefaultWizardModel) WizardPanelFactory
        .getInternationalWizardModel(builder,
            SymphonieWizardConstants.EWIZARD_TITLE,
            SymphonieWizardConstants.EXPORT_WIZARD_ICON);
    exp.getInterPanelData().put(SymphonieWizardConstants.DATA_EX_DIALOG,
        errDisplay);
    exp.getInterPanelData().put(SymphonieWizardConstants.DATA_MANAGER, manager);
    Wizard wesh = new Wizard(frame, exp, builder, new Dimension(600, 400));
    exp.addPanel(WizardPanelFactory.getExportFormatSelectionPanel(builder));
    exp.addPanel(WizardPanelFactory.getExportFileSelectionPanel(builder));
    exp.addPanel(WizardPanelFactory.getExportFinishPanel(wesh, builder));
    return wesh;
  }

  /**
   * Provides an import wizard
   * 
   * @return The created wizard
   */
  private final Wizard getImportWizard() {
    DefaultWizardModel imp = (DefaultWizardModel) WizardPanelFactory
        .getInternationalWizardModel(builder,
            SymphonieWizardConstants.IWIZARD_TITLE,
            SymphonieWizardConstants.IMPORT_WIZARD_ICON);
    imp.getInterPanelData().put(SymphonieWizardConstants.DATA_EX_DIALOG,
        errDisplay);
    imp.getInterPanelData().put(SymphonieWizardConstants.DATA_MANAGER, manager);
    Wizard iWish = new Wizard(frame, imp, builder, new Dimension(600, 400));
    imp.addPanel(WizardPanelFactory.getImportFileSelectionPanel(builder));
    imp.addPanel(WizardPanelFactory.getImportPanel(iWish, builder));
    return iWish;
  }

  // ----------------------------------------------------------------------------
  // View building methods
  // ----------------------------------------------------------------------------

  /**
   * Builds a panel with the student view
   * 
   * @param toolbar
   *          A toolbar for adding some actions
   * @return a <code>JSplitPane</code>
   */
  private final JSplitPane getStudentPane(JToolBar toolbar) {

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    split.setDividerSize(5);

    // Table
    final JTable table = new JTable(studentModel);
    table.setTableHeader(null);
    table.setDefaultRenderer(Object.class, studentModel
        .getFormattableCellRenderer());
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)
            || SwingUtilities.isRightMouseButton(e)) {

          int row = table.rowAtPoint(e.getPoint());
          if ((row % 4) == 2) {

            int col = table.columnAtPoint(e.getPoint());

            Object o = table.getModel().getValueAt(row, col);

            selectedCell = new Pair<Object, Point>(table.getValueAt(row, col),
                new Point(row, col));

            formatCell.setEnabled(true);

            actionFactory.deleteCellFormatAction.setEnabled(currentStudentModel
                .getFormattableCellRenderer().hasFormat(o));
          } else
            formatCell.setEnabled(false);
        }
      }
    });

    // pop up and actions
    final JPopupMenu pop = builder.buildPopupMenu(STUDENTVIEWPOPUP_TITLE);

    pop.add(builder.buildButton(actionFactory
        .getStudentUpdateAction(REFRESH_ICON), UPDATE, ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory.getStudentPrintAction(PRINT_ICON,
        table), PRINT_MENU_ITEM, ButtonType.MENU_ITEM));
    pop.add(new JSeparator());
    pop.add(builder.buildButton(
        actionFactory.getStudentChartAction(CHART_ICON), DISPLAY_CHART,
        ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(formatCell, CELL_MENU_ITEM,
        ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory
        .getDeleteCellFormatAction(EMPTY_ICON), REMOVE_CELL_FORMAT,
        ButtonType.MENU_ITEM));

    // table listeners

    // listener for popup
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          pop.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });

    // listener which saves point location
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          PointSaver.setPoint(e.getPoint());
        }
      }
    });

    JScrollPane scroll1 = new JScrollPane(table);
    split.setRightComponent(scroll1);

    // Tree
    // StudentTreeModel treeModel = new StudentTreeModel(manager);
    final JTree tree = new JTree(studentTreeModel);
    tree.setCellRenderer(new DefaultTreeCellRenderer() {

      private final ImageIcon leafIcon = new ImageIcon(Symphonie.class
          .getResource("icons/student.png"));
      private final ImageIcon rootIcon = new ImageIcon(Symphonie.class
          .getResource("icons/students.png"));

      public Component getTreeCellRendererComponent(JTree tree, Object value,
          boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

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

    tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);
    final JPopupMenu treePop = builder.buildPopupMenu(STUDENTVIEWPOPUP_TITLE);

    addStudentAction = actionFactory.getAddStudentAction(new ImageIcon(
        Symphonie.class.getResource("icons/add_student.png")));
    final AbstractButton addStudent = builder.buildButton(addStudentAction,
        ADD_STUDENT_TITLE, ComponentBuilder.ButtonType.MENU_ITEM);
    addStudent.getAction().setEnabled(false);
    treePop.add(addStudent);
    toolbar.add(createToolbarButton(ADD_STUDENT_TITLE, addStudent, builder));

    treePop.add(builder.buildButton(actionFactory
        .getUpdateStudentTreeAction(REFRESH_ICON), UPDATE,
        ComponentBuilder.ButtonType.MENU_ITEM));

    treePop.add(new JSeparator());

    removeStudentAction = actionFactory.getRemoveStudentAction(new ImageIcon(
        Symphonie.class.getResource("icons/remove_student.png")), tree);

    final AbstractButton removeStudent = builder.buildButton(
        removeStudentAction, REMOVE_STUDENT,
        ComponentBuilder.ButtonType.MENU_ITEM);
    removeStudent.getAction().setEnabled(false);
    treePop.add(removeStudent);
    toolbar.add(createToolbarButton(REMOVE_STUDENT, removeStudent, builder));
    toolbar.addSeparator();

    // listener for popup
    tree.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          treePop.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });

    // listener which selects the right-cliqued row
    tree.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          int n = tree.getRowForLocation(e.getX(), e.getY());

          if (n > 0) tree.setSelectionRow(n);
        }
      }
    });

    // listener which enables the remove button
    tree.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          int n = tree.getRowForLocation(e.getX(), e.getY());
          removeStudent.getAction()
              .setEnabled((n > 0) && logger.isIdentified());
        }
      }
    });

    JScrollPane pane = new JScrollPane(tree);
    split.setLeftComponent(pane);
    tree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {
        Object o = tree.getLastSelectedPathComponent();
        boolean value = (o instanceof Student);

        if (value) {
          ((StudentModel) (table.getModel())).setStudent((Student) o);
          ((DefaultWizardModel) exportW.getModel()).getInterPanelData().put(
              SymphonieWizardConstants.STUDENT_DATA, o);
          updateActions();
        } else
          currentStudentModel.clear();

      }
    });

    logger.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        currentStudentModel = logger.isIdentified() ? adminStudentModel
            : studentModel;
        table.setModel(currentStudentModel);
        updateActions();
      }
    });

    return split;
  }

  /**
   * Builds a panel with the teacher view
   * 
   * @param toolbar
   *          A toolbar to add some actions
   * 
   * @return a <code>JSplitPane</code>
   */
  private final JSplitPane getTeacherPane(JToolBar toolbar) {

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    split.setDividerSize(5);

    // Table
    final JTable table = new JTable(teacherModel);
    table.setTableHeader(null);
    table.setDefaultRenderer(Object.class, teacherModel
        .getFormattableCellRenderer());
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)
            || SwingUtilities.isLeftMouseButton(e)) {
          int row = table.rowAtPoint(e.getPoint());
          if (row >= 3) {
            int col = table.columnAtPoint(e.getPoint());
            Object o = table.getModel().getValueAt(row, col);
            selectedCell = new Pair<Object, Point>(table.getValueAt(row, col),
                new Point(row, col));
            formatCell.setEnabled(true);
            actionFactory.deleteCellFormatAction.setEnabled(currentTeacherModel
                .getFormattableCellRenderer().hasFormat(o));
          } else
            formatCell.setEnabled(false);
        }
      }
    });

    JScrollPane scroll1 = new JScrollPane(table);

    split.setRightComponent(scroll1);

    // popup menu and actions
    final JPopupMenu pop = builder.buildPopupMenu(TEACHERVIEWPOPUP_TITLE);

    pop.add(builder.buildButton(actionFactory.getAddMarkAction(null),
        ADDMARKDIALOG_TITLE, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory
        .getTeacherAddFormulaAction(FORMULA_ICON), ADD_FORMULA,
        ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(new JSeparator());
    pop.add(builder.buildButton(actionFactory
        .getTeacherUpdateAction(REFRESH_ICON), UPDATE,
        ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory.getTeacherPrintAction(PRINT_ICON,
        table), PRINT_MENU_ITEM, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(new JSeparator());
    pop.add(builder.buildButton(actionFactory
        .getTeacherChartAction(CHART2_ICON), DISPLAY_CHART,
        ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(formatCell, CELL_MENU_ITEM,
        ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory
        .getDeleteCellFormatAction(EMPTY_ICON), REMOVE_CELL_FORMAT,
        ButtonType.MENU_ITEM));
    pop.add(new JSeparator());

    final AbstractButton removeColumn = builder.buildButton(actionFactory
        .getRemoveTeacherColumnAction(EMPTY_ICON, table), REMOVE_COLUMN,
        ComponentBuilder.ButtonType.MENU_ITEM);
    pop.add(removeColumn);

    // table listeners

    // listener for popup
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          pop.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });

    // listener which saves point location
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          PointSaver.setPoint(e.getPoint());
        }
      }
    });

    // listener which disables buttons
    table.addMouseListener(new MouseAdapter() {

      private int column;

      public void mousePressed(MouseEvent e) {

        if (SwingUtilities.isRightMouseButton(e)) {
          column = table.columnAtPoint(e.getPoint());
          if (column != table.getColumnCount() - 1 && column > 0)
            removeColumn.setEnabled(true);
          else
            removeColumn.setEnabled(false);
        }
      }
    });

    // Tree
    final JTree tree = new JTree(courseTreeModel);
    tree.getSelectionModel().setSelectionMode(
        TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setCellRenderer(new DefaultTreeCellRenderer() {

      private final Icon leafIcon = new ImageIcon(Symphonie.class
          .getResource("icons/course.png"));
      private final Icon rootIcon = new ImageIcon(Symphonie.class
          .getResource("icons/courses.png"));

      public Component getTreeCellRendererComponent(JTree tree, Object value,
          boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

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

    final JPopupMenu treePop = builder.buildPopupMenu(TEACHERVIEWPOPUP_TITLE);

    addCourseAction = actionFactory.getAddCourseAction(new ImageIcon(
        Symphonie.class.getResource("icons/add_course.png")));
    final AbstractButton addCourse = builder.buildButton(addCourseAction,
        ADDCOURSE, ComponentBuilder.ButtonType.MENU_ITEM);
    addCourse.getAction().setEnabled(false);
    treePop.add(addCourse);
    toolbar.add(createToolbarButton(ADDCOURSE, addCourse, builder));

    treePop.add(builder.buildButton(actionFactory
        .getUpdateCourseTreeAction(REFRESH_ICON), UPDATE,
        ComponentBuilder.ButtonType.MENU_ITEM));
    treePop.add(new JSeparator());

    removeCourseAction = actionFactory.getRemoveCourseAction(new ImageIcon(
        Symphonie.class.getResource("icons/delete_course.png")), tree);
    final AbstractButton removeCourse = builder.buildButton(removeCourseAction,
        REMOVECOURSE, ComponentBuilder.ButtonType.MENU_ITEM);
    removeCourse.getAction().setEnabled(false);
    treePop.add(removeCourse);
    toolbar.add(createToolbarButton(REMOVECOURSE, removeCourse, builder));
    toolbar.addSeparator();

    // listener for popup
    tree.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          treePop.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });

    // listener which selects the right-cliqued row
    tree.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        int n = tree.getRowForLocation(e.getX(), e.getY());
        if (n > 0) {
          tree.setSelectionRow(n);
          updateActions();
        }
      }
    });

    tree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {
        Object o = tree.getLastSelectedPathComponent();
        boolean value = (o instanceof Course);

        if (value) {
          currentTeacherModel.setCourse((Course) o);
          ((DefaultWizardModel) exportW.getModel()).getInterPanelData().put(
              SymphonieWizardConstants.TEACHER_DATA, o);
        } else
          currentTeacherModel.clear();

        updateActions();
      }
    });

    JScrollPane pane = new JScrollPane(tree);
    split.setLeftComponent(pane);

    logger.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        currentTeacherModel = logger.isIdentified() ? adminTeacherModel
            : teacherModel;
        table.setModel(currentTeacherModel);
        updateActions();
      }
    });
    return split;
  }

  /**
   * Builds a panel with the jury view
   * 
   * @return a <code>JScrollPane</code>
   */
  private final JScrollPane getJuryPane() {

    final JTable table = new JTable(juryModel);
    table.setTableHeader(null);
    table.setDefaultRenderer(Object.class, juryModel
        .getFormattableCellRenderer());
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)
            || SwingUtilities.isLeftMouseButton(e)) {
          int row = table.rowAtPoint(e.getPoint());
          if (row >= 3) {
            int col = table.columnAtPoint(e.getPoint());
            Object o = table.getModel().getValueAt(row, col);
            selectedCell = new Pair<Object, Point>(table.getValueAt(row, col),
                new Point(row, col));
            formatCell.setEnabled(true);
            actionFactory.deleteCellFormatAction.setEnabled(currentJuryModel
                .getFormattableCellRenderer().hasFormat(o));
          } else
            formatCell.setEnabled(false);
        }
      }
    });

    final JPopupMenu pop = builder.buildPopupMenu(JURYVIEWPOPUP_TITLE);

    pop.add(builder.buildButton(actionFactory
        .getJuryAddFormulaAction(FORMULA_ICON), ADD_FORMULA,
        ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(new JSeparator());
    pop.add(builder.buildButton(
        actionFactory.getJuryUpdateAction(REFRESH_ICON), UPDATE,
        ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory.getJuryPrintAction(PRINT_ICON,
        table), PRINT_MENU_ITEM, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(new JSeparator());
    pop.add(builder.buildButton(actionFactory.getJuryChartAction(CHART2_ICON),
        DISPLAY_CHART, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(new JSeparator());
    final AbstractButton removeColumn = builder.buildButton(actionFactory
        .getRemoveJuryColumnAction(EMPTY_ICON, table), REMOVE_COLUMN,
        ComponentBuilder.ButtonType.MENU_ITEM);
    pop.add(removeColumn);
    pop.add(builder.buildButton(formatCell, CELL_MENU_ITEM,
        ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory
        .getDeleteCellFormatAction(EMPTY_ICON), REMOVE_CELL_FORMAT,
        ButtonType.MENU_ITEM));

    // listener which displays the popup
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          pop.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    });

    // listener which saves the cursor location
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          PointSaver.setPoint(e.getPoint());
        }
      }
    });

    // listener which disables buttons
    table.addMouseListener(new MouseAdapter() {

      public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
          // JuryModel jm = table.getModel();
          if (juryModel.isColumnFormula(table.columnAtPoint(e.getPoint())))
            removeColumn.setEnabled(true);
          else
            removeColumn.setEnabled(false);
        }
      }
    });

    logger.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        currentJuryModel = logger.isIdentified() ? adminJuryModel : juryModel;
        table.setModel(currentJuryModel);
        updateActions();
      }
    });

    return new JScrollPane(table);
  }

  // ----------------------------------------------------------------------------
  // Content panes
  // ----------------------------------------------------------------------------

  /**
   * Makes the welcome panel
   * 
   * @param content
   *          The panel that contains Symphonie views
   * @param butts
   *          A set of buttons to disable
   * @return The html welcome page as a panel
   */
  private final JPanel getWelcomePagePanel(final JPanel content,
      final JToolBar toolbar, final AbstractButton... butts) {
    JPanel p = new JPanel(new BorderLayout());
    final JEditorPane jeep = new JEditorPane();
    jeep.setEditorKit(new HTMLEditorKit());
    jeep.setEditable(false);

    // Disable
    Action a;
    for (AbstractButton b : butts) {
      a = b.getAction();
      if (a != null)
        a.setEnabled(false);
      else
        b.setEnabled(false);
    }

    // Listen user clicks
    jeep.addHyperlinkListener(new HyperlinkListener() {

      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
          URL lurl = e.getURL();
          String f = (lurl != null) ? lurl.getFile() : e.getDescription();
          setCurrentView(View.valueOf(f.substring(f.lastIndexOf('/') + 1)));
          Action act;
          for (AbstractButton b : butts) {
            act = b.getAction();
            if (act != null)
              act.setEnabled(true);
            else
              b.setEnabled(true);
          }
          content.add(toolbar, BorderLayout.NORTH);
          frame.setContentPane(content);
        }
      }
    });

    // Get html text
    try {
      jeep.setText(WelcomePage.getRenderableHTMLText(builder));
    } catch (IOException e1) {
      return p;
    }

    // Listen builder changes
    builder.addChangeListener(jeep, new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        String text;
        try {
          text = WelcomePage.getRenderableHTMLText(builder);
          jeep.setText(text);
        } catch (IOException e1) {
        }
      }
    });

    JPanel jp = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(20, 20, 20, 20);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;
    jp.setBackground(jeep.getBackground());
    jp.add(jeep, gbc);
    p.add(toolbar, BorderLayout.NORTH);
    p.add(jp, BorderLayout.CENTER);
    return p;
  }

  /**
   * Builds the main panel that contains all program views
   * 
   * @param toolbar
   *          Toolbar for adding some actions
   * @return JPanel
   */
  private final JPanel getContentPane(JToolBar toolbar) {

    JPanel panel = new JPanel(new BorderLayout());

    /* Student JTabbedPane */
    JScrollPane jsp = new JScrollPane(getStudentPane(toolbar));
    tab.add(builder.getValue(VIEW_STUDENT_MENU_ITEM), jsp);
    builder.addChangeListener(jsp, makeTabChangeListener(tab, View.student,
        builder));

    /* Teacher JTabbedPane */
    jsp = new JScrollPane(getTeacherPane(toolbar));
    tab.add(builder.getValue(VIEW_TEACHER_MENU_ITEM), jsp);
    builder.addChangeListener(jsp, makeTabChangeListener(tab, View.teacher,
        builder));

    /* Jury JTabbedPane */
    jsp = new JScrollPane(getJuryPane());
    tab.add(builder.getValue(VIEW_JURY_MENU_ITEM), jsp);
    builder.addChangeListener(jsp, makeTabChangeListener(tab, View.jury,
        builder));

    panel.add(tab);

    tab.getModel().addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        SingleSelectionModel ssm = (SingleSelectionModel) e.getSource();
        switch (ssm.getSelectedIndex()) {
          case 0:
            setCurrentView(View.student);
            break;
          case 1:
            setCurrentView(View.teacher);
            break;
          case 2:
            setCurrentView(View.jury);
            break;
          default:
            throw new IllegalStateException("Invalid tab index");
        }
      }
    });

    return panel;
  }

  // ----------------------------------------------------------------------------
  // Public methods
  // ----------------------------------------------------------------------------

  /**
   * Sets up the application with the given parameters
   * 
   * @param manager
   *          The data manager to use
   * @param rootLogger
   *          The login service
   * @throws DataManagerException
   *           If there's a problem while dealing DB
   * @throws IOException
   *           If there's any i/o error
   */
  public Symphonie(DataManager manager, IdentificationStrategy rootLogger)
      throws DataManagerException, IOException {

    // Data source
    this.manager = manager;

    // Loggin manager
    this.logger = rootLogger;

    // Language tools
    builder = new ComponentBuilder(currentLanguage.getMap());

    // Main frame
    frame = new JFrame(builder.getValue(FRAME_TITLE));

    // Toolbar
    JToolBar toolbar = new JToolBar();
    toolbar.setFloatable(false);
    toolbar.addSeparator();

    // Exception dialog
    errDisplay = new ExceptionDisplayDialog(frame, builder);

    // Wizards
    importW = getImportWizard();
    exportW = getExportWizard();

    // models
    studentModel = new StudentModel(manager, builder);
    teacherModel = new TeacherModel(manager, builder);
    juryModel = new JuryModel(manager, builder);

    adminStudentModel = new AdminStudentModel(manager, builder);
    adminTeacherModel = new AdminTeacherModel(manager, builder);
    adminJuryModel = new AdminJuryModel(manager, builder);

    studentTreeModel = new StudentTreeModel(manager, builder);
    courseTreeModel = new CourseTreeModel(manager, builder);

    // Action factory
    actionFactory = new SymphonieActionFactory(this, builder);
    formatCell = actionFactory.getFormatCellAction(EMPTY_ICON);
    addColumn = actionFactory.getAddMarkAction(new ImageIcon(Symphonie.class
        .getResource("icons/insert_column.png")));
    addColumn.setEnabled(false);

    // Content pane
    JMenu mode = getModeMenu();
    JMenuItem imp = (JMenuItem) builder.buildButton(actionFactory
        .getWizardAction(new ImageIcon(Symphonie.class
            .getResource("icons/import.png")), importW), IMPORT_MENU_ITEM,
        ButtonType.MENU_ITEM);
    imp.getAction().setEnabled(false);
    JMenuItem exp = (JMenuItem) builder.buildButton(actionFactory
        .getWizardAction(new ImageIcon(Symphonie.class
            .getResource("icons/export.png")), exportW), EXPORT_MENU_ITEM,
        ButtonType.MENU_ITEM);
    JMenuItem print = (JMenuItem) builder.buildButton(actionFactory
        .getPrintAction(PRINT_ICON), PRINT_MENU_ITEM, ButtonType.MENU_ITEM);

    JMenuItem exit = (JMenuItem) builder.buildButton(actionFactory
        .getExitAction(new ImageIcon(Symphonie.class
            .getResource("icons/exit.png"))), EXIT_MENU_ITEM,
        ButtonType.MENU_ITEM);

    toolbar.add(createToolbarButton(PRINT_MENU_ITEM, print, builder));
    toolbar.addSeparator();
    toolbar.add(createToolbarButton(IMPORT_MENU_ITEM, imp, builder));
    toolbar.add(createToolbarButton(EXPORT_MENU_ITEM, exp, builder));
    toolbar.addSeparator();

    dischart = new JButton(actionFactory.getChartDisplayAction(CHART_ICON,
        builder));
    dischart.setEnabled(false);

    JMenu admenu = getAdminMenu(toolbar);

    JPanel content = getContentPane(toolbar);
    JPanel welcome = getWelcomePagePanel(content, toolbar, mode, imp, exp,
        print);
    frame.setContentPane((welcome != null) ? welcome : content);

    // Menu bar
    addFormula.setEnabled(false);
    JMenuItem morfula = (JMenuItem) builder.buildButton(addFormula,
        FORMULA_MENU_ITEM, ButtonType.MENU_ITEM);
    frame.setJMenuBar(getMenubar(getFileMenu(exp, imp, print, exit),
        getWindowMenu(mode, getLangMenu()), getInsertMenu(morfula, toolbar),
        admenu));

    // Listen changes in builder for frame title
    builder.addChangeListener(content, new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        frame.setTitle(builder.getValue(FRAME_TITLE));
      }
    });

    currentJuryModel = juryModel;
    currentTeacherModel = teacherModel;
    currentStudentModel = studentModel;

    toolbar.add(createToolbarButton(ADDMARKDIALOG_TITLE, morfula, builder));
    toolbar.addSeparator();

    toolbar.add(dischart);
    toolbar.addSeparator();
    toolbar.add(builder.buildLabel(SETSTEP));
    spinner = new JSpinner(new SpinnerNumberModel(chartStep, 1, 20, 1));
    spinner.setMaximumSize(new Dimension(48, 32));
    spinner.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        chartStep = ((SpinnerNumberModel) (spinner.getModel())).getNumber()
            .intValue();
      }
    });
    spinner.setEnabled(false);

    toolbar.add(spinner);
    toolbar.addSeparator();

    toolbar.add(createToolbarButton(EXIT_MENU_ITEM, exit, builder));

    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {

      public void windowClosed(java.awt.event.WindowEvent e) {
        try {
          if (logger.isIdentified()) logger.logout();
        } catch (IdentificationException e1) {
          errDisplay.showException(e1);
        }
      }

      public void windowClosing(java.awt.event.WindowEvent e) {
        try {
          if (logger.isIdentified()) logger.logout();
        } catch (IdentificationException e1) {
          errDisplay.showException(e1);
        }
      }
    });
  }

  /**
   * Sets up the main application with the default data manager and login
   * strategy
   * 
   * @see #Symphonie(DataManager, IdentificationStrategy)
   * @throws DataManagerException
   *           If there's a problem while dealing DB
   * @throws IOException
   *           If there's any i/o error
   */
  public Symphonie() throws DataManagerException, IOException {
    this(SQLDataManager.getInstance(), new SQLIdentificationStrategy());
  }

  /**
   * Returns main program frame
   * 
   * @return a <code>JFrame</code>
   */
  public JFrame getFrame() {
    return frame;
  }

  /**
   * Returns the current view
   * 
   * @return the current view
   */
  public View getCurrentView() {
    return currentView;
  }

  /**
   * Updates the current view
   * 
   * @param newView
   *          the new view
   */
  public void setCurrentView(View newView) {
    this.currentView = newView;
    tab.setSelectedIndex(currentView.ordinal());
    ((DefaultWizardModel) exportW.getModel()).getInterPanelData().put(
        SymphonieWizardConstants.DATA_VIEW, newView);
    ((DefaultWizardModel) importW.getModel()).getInterPanelData().put(
        SymphonieWizardConstants.DATA_VIEW, newView);

    updateActions();
  }

  /**
   * Updates symphonie actions
   */
  protected final void updateActions() {
    if (currentView == null) return;
    AbstractSymphonieTableModel atm = currentView.getModel(this);
    boolean isStudentView = currentView.equals(View.student);
    boolean isTeacherView = currentView.equals(View.teacher);
    boolean isIdentified = logger.isIdentified();
    addColumn.setEnabled(isTeacherView && !atm.isEmpty());
    addFormula.setEnabled(currentView.isFormulaSupported());
    spinner.setEnabled(!isStudentView);
    dischart.setEnabled(!atm.isEmpty());
    addStudentAction.setEnabled(isStudentView && isIdentified);
    removeStudentAction.setEnabled(isStudentView && isIdentified
        && !atm.isEmpty());
    addCourseAction.setEnabled(isIdentified && isTeacherView);
    removeCourseAction.setEnabled(isTeacherView && isIdentified
        && !atm.isEmpty());
  }

  /**
   * Returns the current language
   * 
   * @return The current language
   */
  public Language getCurrentLanguage() {
    return currentLanguage;
  }

  /**
   * Updates the current language
   * 
   * @param newLanguage
   *          The new language to set
   */
  public void setCurrentLanguage(Language newLanguage) {
    this.currentLanguage = newLanguage;
    SymphoniePreferencesManager.setLanguage(newLanguage);
    try {
      builder.setNameMap(newLanguage.getMap());
    } catch (IOException e) {
      errDisplay.showException(e);
    }
  }

  /**
   * @return Returns the currrentJuryModel.
   */
  public JuryModel getCurrentJuryModel() {
    return currentJuryModel;
  }

  /**
   * @return Returns the currrentStudentModel.
   */
  public StudentModel getCurrentStudentModel() {
    return currentStudentModel;
  }

  /**
   * @return Returns the currrentTeacherModel.
   */
  public TeacherModel getCurrentTeacherModel() {
    return currentTeacherModel;
  }

  /**
   * @return Returns the IdentificationStrategy.
   */
  public IdentificationStrategy getIdentificationStrategy() {
    return logger;
  }

  // ----------------------------------------------------------------------------
  // Static methods
  // ----------------------------------------------------------------------------

  /**
   * Makes a <code>ChangeListener</code> that updates a tab title
   * 
   * @param tabs
   *          The <code>JTabbedPane</code> that contains the view
   * @param v
   *          The view whose title will be updated
   * @return a <code>ChangeListener</code>
   */
  private static final ChangeListener makeTabChangeListener(
      final JTabbedPane tabs, final View v, final ComponentBuilder builder) {
    return new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        tabs.setTitleAt(v.ordinal(), builder.getValue(v.getNameKey()));
      }
    };
  }

  /**
   * Creates an icon-only button that can be used in a toolbar
   * 
   * @param key
   *          The tooltip key, usually the same that is used to internationalize
   *          key that was used to build the button
   * @param del
   *          The button that will serve a data source, and whose action will be
   *          performed by toolbar button
   * @param builder
   *          The builder for internationalization
   * @return a <code>JButton</code>
   */
  private static final JButton createToolbarButton(String key,
      final AbstractButton del, ComponentBuilder builder) {
    final JButton tb = new JButton();
    final Action a = del.getAction();
    tb.setEnabled(a.isEnabled());
    a.addPropertyChangeListener(new PropertyChangeListener() {

      public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Action.NAME))
          tb.setToolTipText((String) evt.getNewValue());
        tb.setEnabled(a.isEnabled());
      }
    });
    tb.setIcon((Icon) a.getValue(Action.SMALL_ICON));
    tb.setToolTipText((String) a.getValue(Action.NAME));
    tb.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        a.actionPerformed(e);
      }
    });
    return tb;
  }

  // ----------------------------------------------------------------------------
  // Static fields
  // ----------------------------------------------------------------------------

  /** A transparent 16x16 icon */
  public static final ImageIcon EMPTY_ICON = new ImageIcon(Symphonie.class
      .getResource("icons/empty.png"));

  /** Chart 16x16 icon */
  public static final ImageIcon CHART_ICON = new ImageIcon(Symphonie.class
      .getResource("icons/chart.png"));

  /** Another chart 16x16 icon */
  public static final ImageIcon CHART2_ICON = new ImageIcon(Symphonie.class
      .getResource("icons/chart2.png"));

  /** Printer 16x16 icon */
  public static final ImageIcon PRINT_ICON = new ImageIcon(Symphonie.class
      .getResource("icons/print.png"));

  /** Printer 16x16 icon */
  public static final ImageIcon FORMULA_ICON = new ImageIcon(Symphonie.class
      .getResource("icons/formula.png"));

  /** Printer 16x16 icon */
  public static final ImageIcon REFRESH_ICON = new ImageIcon(Symphonie.class
      .getResource("icons/reload.png"));

  /**
   * Enum defines languages supported by Symphonie.
   */
  public enum Language {

    FRENCH(new Locale("french")) {

      String getCharset() {
        return ISO88591;
      }
    },
    ENGLISH(new Locale("english")) {

      String getCharset() {
        return USASCII;
      }
    },
    SPANISH(new Locale("spanish")) {

      String getCharset() {
        return ISO88591;
      }
    },
    RUSSIAN(new Locale("russian")) {

      String getCharset() {
        return UTF8;
      }
    };

    /** Language locale */
    private final Locale locale;

    /** Language resources */
    private HashMap<String, String> resources;

    /**
     * Constructor
     * 
     * @param l
     *          The language locale
     */
    Language(Locale l) {
      locale = l;
    }

    /**
     * Returns the language map
     * 
     * @return a <code>HashMap</code>
     * @throws IOException
     *           If there's a problem while reading language file
     */
    HashMap<String, String> getMap() throws IOException {
      if (resources == null)
        resources = TextualResourcesLoader.getResourceMap(FILE_PATTERN,
            getLocale(), getCharset());
      return resources;
    }

    /**
     * Gets the language locale
     * 
     * @return a <code>Locale</code>
     */
    Locale getLocale() {
      return locale;
    }

    /**
     * Returns the charset used to decode the language file
     * 
     * @return The name of the language charset
     */
    abstract String getCharset();

    // ------------------
    // Static fields
    // ------------------

    /** Language key */
    public static final String LANGUAGE_NAME = "language.name";

    /** Language files pattern */
    public static final String FILE_PATTERN = "language/symphonie";

    /** ISO-8859-1 Charset */
    public static final String ISO88591 = "ISO-8859-1";

    /** US-ASCII charset */
    public static final String USASCII = "US-ASCII";

    /** UTF-8 charset */
    public static final String UTF8 = "UTF-8";
  }

  /**
   * Enum represents program views <br>
   * A <code>View</code> can be exported and imported. <br>
   * In order to make abstraction of the export/import implemetations
   * <code>Views</code> use a visitor-like design pattern.
   */
  public enum View {
    student {

      String getNameKey() {
        return VIEW_STUDENT_MENU_ITEM;
      }

      void exportView(DataExporter exporter, DataManager manager,
          Map<Object, Object> data, String output) throws DataExporterException {
        exporter.exportStudentView(output, manager, (Student) data
            .get(SymphonieWizardConstants.STUDENT_DATA));
      }

      void importView(DataImporter importer, DataManager manager, Object input)
          throws DataImporterException {
        importer.importStudentView((String) input, manager);
      }

      void print(Symphonie s, ActionEvent event) {
        s.actionFactory.studentPrintAction.actionPerformed(null);
      }

      void displayChart(Symphonie s, ActionEvent event) {
        s.actionFactory.studentChartAction.actionPerformed(null);
      }

      boolean isFormulaSupported() {
        return false;
      }

      void displayFormulaDialog(Symphonie s, ActionEvent e) {
      }

      AbstractSymphonieTableModel getModel(Symphonie s) {
        return s.getCurrentStudentModel();
      }
    },
    teacher {

      String getNameKey() {
        return VIEW_TEACHER_MENU_ITEM;
      }

      void exportView(DataExporter exporter, DataManager manager,
          Map<Object, Object> data, String output) throws DataExporterException {
        exporter.exportTeacherView(output, manager, (Course) data
            .get(SymphonieWizardConstants.TEACHER_DATA));
      }

      void importView(DataImporter importer, DataManager manager, Object input)
          throws DataImporterException {
        importer.importTeacherView((String) input, manager);
      }

      void print(Symphonie s, ActionEvent event) {
        s.actionFactory.teacherPrintAction.actionPerformed(null);
      }

      void displayChart(Symphonie s, ActionEvent event) {
        s.actionFactory.teacherChartAction.actionPerformed(null);
      }

      void displayFormulaDialog(Symphonie s, ActionEvent e) {
        s.actionFactory.teacherAddFormulaAction.actionPerformed(e);
      }

      AbstractSymphonieTableModel getModel(Symphonie s) {
        return s.getCurrentTeacherModel();
      }
    },
    jury {

      String getNameKey() {
        return VIEW_JURY_MENU_ITEM;
      }

      void exportView(DataExporter exporter, DataManager manager,
          Map<Object, Object> data, String output) throws DataExporterException {
        exporter.exportJuryView(output, manager);
      }

      void importView(DataImporter importer, DataManager manager, Object input)
          throws DataImporterException {
        importer.importJuryView((String) input, manager);
      }

      void print(Symphonie s, ActionEvent event) {
        s.actionFactory.juryPrintAction.actionPerformed(event);
      }

      void displayChart(Symphonie s, ActionEvent event) {
        s.actionFactory.juryChartAction.actionPerformed(event);
      }

      void displayFormulaDialog(Symphonie s, ActionEvent e) {
        s.actionFactory.juryAddFormulaAction.actionPerformed(e);
      }

      AbstractSymphonieTableModel getModel(Symphonie s) {
        return s.getCurrentJuryModel();
      }
    };

    /**
     * Returns view's name key
     * 
     * @return a <code>String</code>
     */
    abstract String getNameKey();

    /**
     * Exports view usign the given exporter
     * 
     * @param exporter
     *          The exporter object
     * @param manager
     *          The data manager that is the source view
     * @param data
     *          Some extra data that may be needed by the export process
     * @param output
     *          The output file, note that export can be done to something else
     *          than a file and the programme can use this <code>String</code>
     *          at his convenience.
     * @throws DataExporterException
     *           If the exporter reports a problem
     */
    abstract void exportView(DataExporter exporter, DataManager manager,
        Map<Object, Object> data, String output) throws DataExporterException;

    /**
     * Imports view using the given importer
     * 
     * @param importer
     *          The importer object
     * @param manager
     *          The data manager that will receive imported data
     * @param input
     *          Input can be any object that the programmers may need,
     *          typically, it's a file name.
     * @throws DataImporterException
     *           If the importer reports any problem
     */
    abstract void importView(DataImporter importer, DataManager manager,
        Object input) throws DataImporterException;

    /**
     * Prints the view
     * 
     * @param s
     *          The symphonie instance
     * @param event
     *          The event that started the action
     */
    abstract void print(Symphonie s, ActionEvent event);

    /**
     * Displays the chart's view
     * 
     * @param s
     *          The symphonie instance
     * @param e
     *          Event that started the action
     */
    abstract void displayChart(Symphonie s, ActionEvent e);

    /**
     * Tells whether this view supports formula adding
     * 
     * @return true or false
     */
    boolean isFormulaSupported() {
      return true;
    }

    /**
     * Displays the formula adding dialog (if supported)
     * 
     * @param s
     *          The symphonie instance
     * @param e
     *          Event that started the action
     */
    abstract void displayFormulaDialog(Symphonie s, ActionEvent e);

    /**
     * Returns the table model associated to the view
     * 
     * @param s
     *          The symphonie instance
     * @return a <code>TableModel</code>
     */
    abstract AbstractSymphonieTableModel getModel(Symphonie s);

  }

  // ----------------------------------------------------------------------------
  // Actions that depends on views
  // ----------------------------------------------------------------------------

  /** Global add formula action (depends on views) */
  protected final AbstractAction addFormula = new AbstractAction("erf",
      FORMULA_ICON) {

    public void actionPerformed(ActionEvent e) {
      currentView.displayFormulaDialog(Symphonie.this, e);
    }
  };

  /** Add column action (depends on views) */
  protected AbstractAction addColumn;

  /** Format cell action (depends on selected cell) */
  protected AbstractAction formatCell;

  /** Cell selected in current view */
  protected Pair<Object, Point> selectedCell;

  /** Step interval for charts */
  protected int chartStep = 5;

  /** Spinner for selecting chart interval */
  protected final JSpinner spinner;

  /** Button that allows user to display chart */
  protected final JButton dischart;

  /** Action that allows users to add a student */
  protected AbstractAction addStudentAction;

  /** Action that allows user to remove a student */
  protected AbstractAction removeStudentAction;

  /** Action that allows user to add a course */
  protected AbstractAction addCourseAction;

  /** Action that allows user to remove a course */
  protected AbstractAction removeCourseAction;

  /**
   * Returns the treemodel for teacher view
   * 
   * @return a CourseTreeModel
   */
  public CourseTreeModel getCourseTreeModel() {
    return courseTreeModel;
  }

  /**
   * Returns the tree model for student view
   * 
   * @return a StudentTreeModel
   */
  public StudentTreeModel getStudentTreeModel() {
    return studentTreeModel;
  }

  /**
   * @return Returns the chartStep.
   */
  public int getChartStep() {
    return chartStep;
  }
}
