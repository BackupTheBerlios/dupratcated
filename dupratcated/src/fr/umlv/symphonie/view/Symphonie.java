/*
 * This file is part of Symphonie
 * Created : 17 févr. 2005 21:30:59
 */

package fr.umlv.symphonie.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
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
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SingleSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.DefaultTreeCellRenderer;

import fr.umlv.symphonie.data.Course;
import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.DataManagerException;
import fr.umlv.symphonie.data.SQLDataManager;
import fr.umlv.symphonie.data.Student;
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
import fr.umlv.symphonie.util.SymphoniePreferencesManager;
import fr.umlv.symphonie.util.TextualResourcesLoader;
import fr.umlv.symphonie.util.ComponentBuilder.ButtonType;
import fr.umlv.symphonie.util.dataexport.DataExporter;
import fr.umlv.symphonie.util.dataexport.DataExporterException;
import fr.umlv.symphonie.util.dataimport.DataImporter;
import fr.umlv.symphonie.util.dataimport.DataImporterException;
import fr.umlv.symphonie.util.identification.IdentificationStrategy;
import fr.umlv.symphonie.util.identification.SQLIdentificationStrategy;
import fr.umlv.symphonie.util.wizard.DefaultWizardModel;
import fr.umlv.symphonie.util.wizard.Wizard;
import static fr.umlv.symphonie.view.SymphonieConstants.ADDMARKDIALOG_TITLE;
import static fr.umlv.symphonie.view.SymphonieConstants.ADD_FORMULA;
import static fr.umlv.symphonie.view.SymphonieConstants.ADMIN_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.CELL_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.CHANGE_VIEW_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.CONNECT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.DB_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.DISPLAY_CHART;
import static fr.umlv.symphonie.view.SymphonieConstants.EXIT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.EXPORT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.FILE_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.FORMAT_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.FORMULA_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.FRAME_TITLE;
import static fr.umlv.symphonie.view.SymphonieConstants.IMPORT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.INSERT_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.LANGUAGE_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.LOGOUT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.PRINT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.PWD_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.REMOVE_COLUMN;
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
    mode.setIcon(EMPTYICON);

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
   * Builds the "Format" menu
   * 
   * @return a <code>JMenu</code>
   */
  private final JMenu getFormatMenu() {
    JMenu format = (JMenu) builder.buildButton(FORMAT_MENU, ButtonType.MENU);

    Action f_cell = actionFactory.getFormulaCellAction(null);

    format.add(builder
        .buildButton(f_cell, CELL_MENU_ITEM, ButtonType.MENU_ITEM));

    return format;
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
   * @return a <code>JSplitPane</code>
   */
  private final JSplitPane getStudentPane() {

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    // Table
    final JTable table = new JTable(studentModel);
    table.setTableHeader(null);
    table.setDefaultRenderer(Object.class, studentModel.getFormattableCellRenderer());
    table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

      public void valueChanged(ListSelectionEvent e) {
//        if (!e.getValueIsAdjusting()) {
//         Object o = table.getValueAt(table.getSelectedRow(), e.getFirstIndex());
//         System.out.println(o);
//         selectedObjects.clear();
//         selectedObjects.add(o);
//        }        
      }        
    });
    
    // pop up and actions
    final JPopupMenu pop = builder
        .buildPopupMenu(SymphonieConstants.STUDENTVIEWPOPUP_TITLE);

    pop.add(builder.buildButton(actionFactory.getStudentUpdateAction(null),
        UPDATE, ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory
        .getStudentPrintAction(null, table), PRINT_MENU_ITEM,
        ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory.getStudentChartAction(null),
        DISPLAY_CHART, ButtonType.MENU_ITEM));

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

      private final Icon leafIcon = new ImageIcon(StudentTreeModel.class
          .getResource("../view/icons/student.png"));
      private final Icon rootIcon = new ImageIcon(StudentTreeModel.class
          .getResource("../view/icons/students.png"));

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

    final JPopupMenu treePop = builder
        .buildPopupMenu(SymphonieConstants.STUDENTVIEWPOPUP_TITLE);

    final AbstractButton addStudent = builder.buildButton(actionFactory
        .getAddStudentAction(null), SymphonieConstants.ADD_STUDENT_TITLE,
        ComponentBuilder.ButtonType.MENU_ITEM);
    treePop.add(addStudent);
    
    treePop.add(builder.buildButton(actionFactory.getUpdateStudentTreeAction(null), SymphonieConstants.UPDATE, ComponentBuilder.ButtonType.MENU_ITEM));
    
    
    final AbstractButton removeStudent = builder.buildButton(actionFactory
        .getRemoveStudentAction(null, tree), SymphonieConstants.REMOVE_STUDENT,
        ComponentBuilder.ButtonType.MENU_ITEM);
    treePop.add(removeStudent);


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

          if (n > 0)
            removeStudent.setEnabled(true);
          
          else removeStudent.setEnabled(false);
        }
      }
    });
    
    JScrollPane pane = new JScrollPane(tree);
    split.setLeftComponent(pane);
    tree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {
        Object o = tree.getLastSelectedPathComponent();
        if (o instanceof Student) {
          ((StudentModel) (table.getModel())).setStudent((Student) o);
          ((DefaultWizardModel) exportW.getModel()).getInterPanelData().put(
              SymphonieWizardConstants.STUDENT_DATA, o);
        }
      }
    });

    logger.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        currentStudentModel = logger.isIdentified() ? adminStudentModel
            : studentModel;
        table.setModel(currentStudentModel);
      }
    });

    return split;
  }

  /**
   * Builds a panel with the teacher view
   * 
   * @return a <code>JSplitPane</code>
   */
  private final JSplitPane getTeacherPane() {

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    // Table
//    final TeacherModel teacherModel = TeacherModel.getInstance(manager);
    final JTable table = new JTable(teacherModel);
    table.setTableHeader(null);
    table.setDefaultRenderer(Object.class, teacherModel.getFormattableCellRenderer());

    JScrollPane scroll1 = new JScrollPane(table);

    split.setRightComponent(scroll1);

    // popup menu and actions
    final JPopupMenu pop = builder
        .buildPopupMenu(SymphonieConstants.TEACHERVIEWPOPUP_TITLE);

    pop.add(builder.buildButton(actionFactory.getAddMarkAction(null),
        ADDMARKDIALOG_TITLE, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory.getTeacherAddFormulaAction(null),
        ADD_FORMULA, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory.getTeacherUpdateAction(null),
        UPDATE, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory
        .getTeacherPrintAction(null, table), PRINT_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory.getTeacherChartAction(null),
        DISPLAY_CHART, ComponentBuilder.ButtonType.MENU_ITEM));

    final AbstractButton removeColumn = builder.buildButton(actionFactory
        .getRemoveTeacherColumnAction(null, table), REMOVE_COLUMN,
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
    // CourseTreeModel courseModel = new CourseTreeModel(manager);
    final JTree tree = new JTree(courseTreeModel);
    tree.setCellRenderer(new DefaultTreeCellRenderer() {

      private final Icon leafIcon = new ImageIcon(StudentTreeModel.class
          .getResource("../view/icons/course.png"));
      private final Icon rootIcon = new ImageIcon(StudentTreeModel.class
          .getResource("../view/icons/courses.png"));

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

    
    
    final JPopupMenu treePop = builder
        .buildPopupMenu(SymphonieConstants.TEACHERVIEWPOPUP_TITLE);

    final AbstractButton addCourse = builder.buildButton(actionFactory
        .getAddCourseAction(null), SymphonieConstants.ADDCOURSE,
        ComponentBuilder.ButtonType.MENU_ITEM);
    treePop.add(addCourse);

    treePop.add(builder.buildButton(actionFactory
        .getUpdateCourseTreeAction(null), SymphonieConstants.UPDATE,
        ComponentBuilder.ButtonType.MENU_ITEM));

    final AbstractButton removeCourse = builder.buildButton(actionFactory
        .getRemoveCourseAction(null, tree), SymphonieConstants.REMOVECOURSE,
        ComponentBuilder.ButtonType.MENU_ITEM);
    treePop.add(removeCourse);

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

          if (n > 0)
            removeCourse.setEnabled(true);

          else
            removeCourse.setEnabled(false);
        }
      }
    });  
    
    
    tree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {
        Object o = tree.getLastSelectedPathComponent();

        if (o instanceof Course) {
          ((TeacherModel) table.getModel()).setCourse((Course) o);
          ((DefaultWizardModel) exportW.getModel()).getInterPanelData().put(
              SymphonieWizardConstants.TEACHER_DATA, o);
        }
      }
    });

    JScrollPane pane = new JScrollPane(tree);
    split.setLeftComponent(pane);

    logger.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        currentTeacherModel = logger.isIdentified() ? adminTeacherModel
            : teacherModel;
        table.setModel(currentTeacherModel);
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

    final JPopupMenu pop = builder
        .buildPopupMenu(SymphonieConstants.JURYVIEWPOPUP_TITLE);

    pop.add(builder.buildButton(actionFactory.getJuryAddFormulaAction(null),
        SymphonieConstants.ADD_FORMULA, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory.getJuryUpdateAction(null),
        SymphonieConstants.UPDATE, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(actionFactory.getJuryPrintAction(null, table),
        SymphonieConstants.PRINT_MENU_ITEM,
        ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder
        .buildButton(actionFactory.getJuryChartAction(null),
            SymphonieConstants.DISPLAY_CHART,
            ComponentBuilder.ButtonType.MENU_ITEM));

    final AbstractButton removeColumn = builder
        .buildButton(actionFactory.getRemoveJuryColumnAction(null, table),
            SymphonieConstants.REMOVE_COLUMN,
            ComponentBuilder.ButtonType.MENU_ITEM);
    pop.add(removeColumn);

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

    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

      public Component getTableCellRendererComponent(JTable table,
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

    logger.addChangeListener(new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        currentJuryModel = logger.isIdentified() ? adminJuryModel : juryModel;
        table.setModel(currentJuryModel);
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
    for (AbstractButton b : butts)
      b.setEnabled(false);

    // Listen user clicks
    jeep.addHyperlinkListener(new HyperlinkListener() {

      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
          String f = e.getURL().getFile();
          setCurrentView(View.valueOf(f.substring(f.lastIndexOf('/') + 1)));
          for (AbstractButton b : butts)
            b.setEnabled(true);
          content.add(toolbar, BorderLayout.NORTH);
          frame.setContentPane(content);
        }
      }
    });

    // Get html text
    try {
      jeep.setText(WelcomePage.getRenderableHTMLText(builder));
    } catch (IOException e1) {
      return null;
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
   * @return JPanel
   */
  private final JPanel getContentPane() {

    JPanel panel = new JPanel(new BorderLayout());

    /* Student JTabbedPane */
    JScrollPane jsp = new JScrollPane(getStudentPane());
    tab.add(builder.getValue(VIEW_STUDENT_MENU_ITEM), jsp);
    builder.addChangeListener(jsp, makeTabChangeListener(tab, View.student,
        builder));

    /* Teacher JTabbedPane */
    jsp = new JScrollPane(getTeacherPane());
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
    studentModel = StudentModel.getInstance(manager);
    teacherModel = TeacherModel.getInstance(manager);
    juryModel = JuryModel.getInstance(manager);

    adminStudentModel = AdminStudentModel.getInstance(manager);
    adminTeacherModel = AdminTeacherModel.getInstance(manager);
    adminJuryModel = AdminJuryModel.getInstance(manager);

    studentTreeModel = StudentTreeModel.getInstance(manager);
    courseTreeModel = CourseTreeModel.getInstance(manager);

    // Action factory
    actionFactory = new SymphonieActionFactory(this, builder);
    addColumn = actionFactory.getAddMarkAction(new ImageIcon(Symphonie.class
        .getResource("icons/insert_column.png")));
    addColumn.setEnabled(false);

    // Content pane
    JMenu mode = getModeMenu();
    JMenuItem imp = (JMenuItem) builder.buildButton(actionFactory
        .getWizardAction(new ImageIcon(Symphonie.class
            .getResource("icons/import.png")), importW), IMPORT_MENU_ITEM,
        ButtonType.MENU_ITEM);
    JMenuItem exp = (JMenuItem) builder.buildButton(actionFactory
        .getWizardAction(new ImageIcon(Symphonie.class
            .getResource("icons/export.png")), exportW), EXPORT_MENU_ITEM,
        ButtonType.MENU_ITEM);
    JMenuItem print = (JMenuItem) builder.buildButton(actionFactory
        .getPrintAction(new ImageIcon(Symphonie.class
            .getResource("icons/print.png"))), PRINT_MENU_ITEM,
        ButtonType.MENU_ITEM);

    JMenuItem exit = (JMenuItem) builder.buildButton(actionFactory
        .getExitAction(new ImageIcon(Symphonie.class
            .getResource("icons/exit.png"))), EXIT_MENU_ITEM,
        ButtonType.MENU_ITEM);

    toolbar.add(createToolbarButton(PRINT_MENU_ITEM, print, builder));
    toolbar.addSeparator();
    toolbar.add(createToolbarButton(IMPORT_MENU_ITEM, imp, builder));
    toolbar.add(createToolbarButton(EXPORT_MENU_ITEM, exp, builder));
    toolbar.addSeparator();

    JButton dischart = new JButton(actionFactory.getChartDisplayAction(
        CHARTICON, builder));
    JPanel content = getContentPane();
    JPanel welcome = getWelcomePagePanel(content, toolbar, mode, imp, exp,
        print, dischart);
    frame.setContentPane((welcome != null) ? welcome : content);

    // Menu bar
    addFormula.setEnabled(false);
    JMenuItem morfula = (JMenuItem) builder.buildButton(addFormula,
        FORMULA_MENU_ITEM, ButtonType.MENU_ITEM);
    JMenu admenu = getAdminMenu(toolbar);
    frame.setJMenuBar(getMenubar(getFileMenu(exp, imp, print, exit),
        getWindowMenu(mode, getLangMenu()), getFormatMenu(), getInsertMenu(
            morfula, toolbar), admenu));

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
    toolbar.add(dischart);
    toolbar.addSeparator();
    toolbar.add(createToolbarButton(EXIT_MENU_ITEM, exit, builder));

    frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
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
    addFormula.setEnabled(newView.isFormulaSupported());
    addColumn.setEnabled(newView.equals(View.teacher));
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
  public static final ImageIcon EMPTYICON = new ImageIcon(Symphonie.class
      .getResource("icons/empty.png"));

  /** Chart 16x16 icon */
  public static final ImageIcon CHARTICON = new ImageIcon(Symphonie.class
      .getResource("icons/chart.png"));

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
  }

  // ----------------------------------------------------------------------------
  // Actions that depends on views
  // ----------------------------------------------------------------------------

  /** Global add formula action (depends on views) */
  private final AbstractAction addFormula = new AbstractAction("erf",
      new ImageIcon(Symphonie.class.getResource("icons/formula.png"))) {

    public void actionPerformed(ActionEvent e) {
      currentView.displayFormulaDialog(Symphonie.this, e);
    }
  };

  /** Add column action (depends on views) */
  private AbstractAction addColumn;

  /** List of selected objects in current view */
  protected final ArrayList<Object> selectedObjects = new ArrayList<Object>();
  
  public CourseTreeModel getCourseTreeModel() {
    return courseTreeModel;
  }

  public StudentTreeModel getStudentTreeModel() {
    return studentTreeModel;
  }
}
