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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
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
import fr.umlv.symphonie.model.CourseTreeModel;
import fr.umlv.symphonie.model.JuryModel;
import fr.umlv.symphonie.model.StudentModel;
import fr.umlv.symphonie.model.StudentTreeModel;
import fr.umlv.symphonie.model.TeacherModel;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ExceptionDisplayDialog;
import fr.umlv.symphonie.util.TextualResourcesLoader;
import fr.umlv.symphonie.util.ComponentBuilder.ButtonType;
import fr.umlv.symphonie.util.dataexport.DataExporter;
import fr.umlv.symphonie.util.dataexport.DataExporterException;
import fr.umlv.symphonie.util.dataimport.DataImporter;
import fr.umlv.symphonie.util.dataimport.DataImporterException;
import fr.umlv.symphonie.util.wizard.DefaultWizardModel;
import fr.umlv.symphonie.util.wizard.Wizard;
import static fr.umlv.symphonie.view.SymphonieConstants.ADMIN_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.CELL_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.CHANGE_VIEW_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.CONNECT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.DB_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.EXIT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.EXPORT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.FILE_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.FORMAT_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.FORMULA_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.FRAME_TITLE;
import static fr.umlv.symphonie.view.SymphonieConstants.IMPORT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.INSERT_COLUMN_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.INSERT_LINE_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.INSERT_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.LANGUAGE_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.PRINT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.PWD_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.VIEW_JURY_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.VIEW_STUDENT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.VIEW_TEACHER_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.WINDOW_MENU;
import static fr.umlv.symphonie.view.SymphonieConstants.ADDMARKDIALOG_TITLE;
import static fr.umlv.symphonie.view.SymphonieConstants.ADD_FORMULA;
import static fr.umlv.symphonie.view.SymphonieConstants.UPDATE;
import static fr.umlv.symphonie.view.SymphonieConstants.PRINT_MENU_ITEM;
import static fr.umlv.symphonie.view.SymphonieConstants.DISPLAY_CHART;
import static fr.umlv.symphonie.view.SymphonieConstants.REMOVE_COLUMN;
import static fr.umlv.symphonie.view.SymphonieActionFactory.*;
import static fr.umlv.symphonie.util.ComponentBuilder.ButtonType;
import fr.umlv.symphonie.view.cells.CellRendererFactory;

public class Symphonie {

  /** Symphonie data source */
  private final DataManager manager;

  /** The international builder */
  private ComponentBuilder builder;

  /** The default language */
  private Language currentLanguage = Language.valueOf("FRENCH");

  /** The main frame */
  private JFrame frame;

  /** The views tabbed pane */
  protected JTabbedPane tab;

  /** Exception display tool */
  public final ExceptionDisplayDialog errDisplay;

  /** Current view */
  private View currentView;

  /** Export wizard model */
  private final Wizard exportW;

  /** Import wizard model */
  private final Wizard importW;

  // ----------------------------------------------------------------------------
  // Menu building methods
  // ----------------------------------------------------------------------------

  /**
   * Builds the "File" frame menu
   * 
   * @return a <code>JMenu</code>
   */
  private final JMenu getFileMenu() {
    JMenu file = (JMenu) builder.buildButton(FILE_MENU, ButtonType.MENU);

    /*
     * Actions Menu "File"
     * *******************************************************
     */

    Action a_import = SymphonieActionFactory.getWizardAction(new ImageIcon(
        Symphonie.class.getResource("icons/import.png")), importW);

    Action export = SymphonieActionFactory.getWizardAction(new ImageIcon(
        Symphonie.class.getResource("icons/export.png")), exportW);

    Action print = SymphonieActionFactory.getPrintAction(new ImageIcon(
        Symphonie.class.getResource("icons/print.png")));

    Action exit = SymphonieActionFactory.getExitAction(new ImageIcon(
        Symphonie.class.getResource("icons/exit.png")));

    /*
     * Items Menu "File"
     * *********************************************************
     */

    file.add(builder.buildButton(a_import, IMPORT_MENU_ITEM,
        ButtonType.MENU_ITEM));
    file.add(builder
        .buildButton(export, EXPORT_MENU_ITEM, ButtonType.MENU_ITEM));

    file.add(new JSeparator());

    file.add(builder.buildButton(print, PRINT_MENU_ITEM, ButtonType.MENU_ITEM));

    file.add(new JSeparator());

    file.add(builder.buildButton(exit, EXIT_MENU_ITEM, ButtonType.MENU_ITEM));

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
      lBox = new JCheckBoxMenuItem(SymphonieActionFactory
          .getLanguageChangeAction(this, l));
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
    JCheckBoxMenuItem vBox;
    javax.swing.AbstractAction a;
    for (View v : View.values()) {
      a = SymphonieActionFactory.getModeChangeAction(this, v);
      vBox = (JCheckBoxMenuItem) builder.buildButton(a, v.getNameKey(),
          ButtonType.CHECK_BOX_MENU_ITEM);
      g.add(vBox);
      mode.add(vBox);
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

    /* Actions ******************************************************* */
    Action formula = SymphonieActionFactory.getFormulaAction(new ImageIcon(
        Symphonie.class.getResource("icons/formula.png")), frame, builder);
    Action f_cell = SymphonieActionFactory.getFormulaCellAction(EMPTYICON,
        frame, builder);

    /* Items********************************************************* */
    format.add(builder.buildButton(formula, FORMULA_MENU_ITEM,
        ButtonType.MENU_ITEM));
    format.add(builder
        .buildButton(f_cell, CELL_MENU_ITEM, ButtonType.MENU_ITEM));

    return format;
  }

  /**
   * Builds the "Insert" menu
   * 
   * @return a <code>JMenu</code>
   */
  private final JMenu getInsertMenu() {
    JMenu insert = (JMenu) builder.buildButton(INSERT_MENU, ButtonType.MENU);
    /* Actions ******************************************************* */
    Action column = SymphonieActionFactory.getColumnAction(new ImageIcon(
        Symphonie.class.getResource("icons/insert_column.png")));
    Action line = SymphonieActionFactory.getLineAction(new ImageIcon(
        Symphonie.class.getResource("icons/insert_line.png")));

    /* Items********************************************************* */
    insert.add(builder.buildButton(column, INSERT_COLUMN_MENU_ITEM,
        ButtonType.MENU_ITEM));
    insert.add(builder.buildButton(line, INSERT_LINE_MENU_ITEM,
        ButtonType.MENU_ITEM));
    return insert;
  }

  /**
   * Builds the "Admin" menu
   * 
   * @return a <code>JMenu</code>
   */
  private final JMenu getAdminMenu() {
    JMenu admin = (JMenu) builder.buildButton(ADMIN_MENU, ButtonType.MENU);
    /* Actions ******************************************************* */
    Action connect = SymphonieActionFactory.getConnectAction(new ImageIcon(
        Symphonie.class.getResource("icons/admin.png")), builder);
    Action db = SymphonieActionFactory.getDBAction(new ImageIcon(
        Symphonie.class.getResource("icons/db.png")), frame, builder);
    Action pwd = SymphonieActionFactory.getPwdAction(new ImageIcon(
        Symphonie.class.getResource("icons/pwd.png")), frame, builder);

    /* Items********************************************************* */
    admin.add(builder.buildButton(connect, CONNECT_MENU_ITEM,
        ButtonType.MENU_ITEM));
    admin.add(builder.buildButton(db, DB_MENU_ITEM, ButtonType.MENU_ITEM));
    admin.add(builder.buildButton(pwd, PWD_MENU_ITEM, ButtonType.MENU_ITEM));
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

  /**
   * Builds the toolbar
   * 
   * @return a ready-to-use <code>JToolBar</code>
   */
  private final JToolBar getToolbar() {
    JToolBar toolbar = new JToolBar();
    toolbar.add(SymphonieActionFactory.getConnectAction(new ImageIcon(
        Symphonie.class.getResource("icons/connect.png")), builder));
    toolbar.add(SymphonieActionFactory.getDBAction(new ImageIcon(
        Symphonie.class.getResource("icons/db.png")), frame, builder));

    toolbar.setFloatable(false);

    return toolbar;
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

    StudentModel studentModel = new StudentModel(manager);
    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    // Table
    final JTable table = new JTable(studentModel);
    table.setTableHeader(null);
    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

      public Component getTableCellRendererComponent(JTable table,
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

    // Tree
    StudentTreeModel treeModel = new StudentTreeModel(manager);
    final JTree tree = new JTree(treeModel);
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

    JScrollPane pane = new JScrollPane(tree);
    split.setLeftComponent(pane);
    tree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {
        Object o = tree.getLastSelectedPathComponent();
        if (o instanceof Student) {
          ((StudentModel) (table.getModel())).setStudent((Student) o);
          ((DefaultWizardModel) exportW.getModel()).getInterPanelData().put(
              SymphonieWizardConstants.DATA_EXPORTABLE, o);
        }
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
    final TeacherModel teacherModel = TeacherModel.getInstance(manager);
    final JTable table = new JTable(teacherModel);
    table.setTableHeader(null);
    table.setDefaultRenderer(Object.class, CellRendererFactory
        .getTeacherModelCellRenderer(teacherModel.getFormattedObjects()));

    JScrollPane scroll1 = new JScrollPane(table);

    split.setRightComponent(scroll1);

    
    // popup menu and actions
    final JPopupMenu pop = builder.buildPopupMenu(SymphonieConstants.TEACHERVIEWPOPUP_TITLE);
    
    pop.add(builder.buildButton(getAddMarkAction(null,frame, builder ), ADDMARKDIALOG_TITLE, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(getTeacherAddFormulaAction(null, frame, builder), ADD_FORMULA, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(getTeacherUpdateAction(null), UPDATE, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(getTeacherPrintAction(null, table), PRINT_MENU_ITEM, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(getTeacherChartAction(null, frame), DISPLAY_CHART, ComponentBuilder.ButtonType.MENU_ITEM));
    
    final AbstractButton removeColumn = builder.buildButton(getRemoveTeacherColumnAction(null, table), REMOVE_COLUMN, ComponentBuilder.ButtonType.MENU_ITEM);
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
          if (column != table.getColumnCount() -1 && column > 0)
            removeColumn.setEnabled(true);
          else removeColumn.setEnabled(false);
        }
      }
    });
    
    
    // Tree
    CourseTreeModel courseModel = new CourseTreeModel(manager);
    final JTree tree = new JTree(courseModel);
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

    tree.addTreeSelectionListener(new TreeSelectionListener() {

      public void valueChanged(TreeSelectionEvent e) {
        Object o = tree.getLastSelectedPathComponent();

        if (o instanceof Course) {
          System.out.println("on a selectionne une matiere !");
          ((TeacherModel) table.getModel()).setCourse((Course) o);
          ((DefaultWizardModel) exportW.getModel()).getInterPanelData().put(
              SymphonieWizardConstants.DATA_EXPORTABLE, o);
        }
      }
    });

    JScrollPane pane = new JScrollPane(tree);
    split.setLeftComponent(pane);
    return split;
  }

  /**
   * Builds a panel with the jury view
   * 
   * @return a <code>JScrollPane</code>
   */
  private final JScrollPane getJuryPane() {
    final JuryModel model = JuryModel.getInstance(manager);
    final JTable table = new JTable(model);
    table.setTableHeader(null);
    
    final JPopupMenu pop = builder.buildPopupMenu(SymphonieConstants.JURYVIEWPOPUP_TITLE);
    
    pop.add(builder.buildButton(SymphonieActionFactory.getJuryAddFormulaAction(null, frame, builder),SymphonieConstants.ADD_FORMULA, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(SymphonieActionFactory.getJuryUpdateAction(null), SymphonieConstants.UPDATE, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(SymphonieActionFactory.getJuryPrintAction(null, table), SymphonieConstants.PRINT_MENU_ITEM, ComponentBuilder.ButtonType.MENU_ITEM));
    pop.add(builder.buildButton(SymphonieActionFactory.getJuryChartAction(null, frame), SymphonieConstants.DISPLAY_CHART, ComponentBuilder.ButtonType.MENU_ITEM));
    
    final AbstractButton removeColumn = builder.buildButton(SymphonieActionFactory.getRemoveJuryColumnAction(null, table), SymphonieConstants.REMOVE_COLUMN, ComponentBuilder.ButtonType.MENU_ITEM);
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
          if (model.isColumnFormula(table.columnAtPoint(e.getPoint())))
            removeColumn.setEnabled(true);
          else removeColumn.setEnabled(false);
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
   * @param mode
   *          The mode menu
   * @return The html welcome page as a panel
   */
  private final JPanel getWelcomePagePanel(final JPanel content,
      final JMenu mode) {
    JPanel p = new JPanel(new GridBagLayout());
    final JEditorPane jeep = new JEditorPane();
    jeep.setEditorKit(new HTMLEditorKit());
    jeep.setEditable(false);

    // Listen user clicks
    jeep.addHyperlinkListener(new HyperlinkListener() {

      public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType().equals(HyperlinkEvent.EventType.ACTIVATED)) {
          String f = e.getURL().getFile();
          setCurrentView(View.valueOf(f.substring(f.lastIndexOf('/') + 1)));
          mode.setEnabled(true);
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

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    p.add(jeep, gbc);
    return p;
  }

  /**
   * Builds the main panel that contains all program views
   * 
   * @return JPanel
   */
  private final JPanel getContentPane() {

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(getToolbar(), BorderLayout.NORTH);

    tab = new JTabbedPane(JTabbedPane.BOTTOM, JTabbedPane.SCROLL_TAB_LAYOUT);

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

    return panel;
  }

  // ----------------------------------------------------------------------------
  // Public methods
  // ----------------------------------------------------------------------------

  /**
   * Sets up the main application
   * 
   * @throws DataManagerException
   *           If there's a problem while dealing DB
   * @throws IOException
   *           If there's any i/o error
   */
  public Symphonie() throws DataManagerException, IOException {

    // Data source
    manager = SQLDataManager.getInstance();

    // Language tools
    builder = new ComponentBuilder(currentLanguage.getMap());

    // Main frame
    frame = new JFrame(builder.getValue(FRAME_TITLE));

    // Exception dialog
    errDisplay = new ExceptionDisplayDialog(frame, builder);

    // Wizards
    importW = getImportWizard();
    exportW = getExportWizard();

    // Content pane
    JMenu mode = getModeMenu();
    JPanel content = getContentPane();
    JPanel welcome = getWelcomePagePanel(content, mode);
    frame.setContentPane((welcome != null) ? welcome : content);

    // Menu bar
    frame.setJMenuBar(getMenubar(getFileMenu(), getWindowMenu(mode,
        getLangMenu()), getFormatMenu(), getInsertMenu(), getAdminMenu()));

    // Listen changes in builder for frame title
    builder.addChangeListener(content, new ChangeListener() {

      public void stateChanged(ChangeEvent e) {
        frame.setTitle(builder.getValue(FRAME_TITLE));
      }
    });
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
    if (newView.equals(View.jury))
      ((DefaultWizardModel) exportW.getModel()).getInterPanelData().remove(
          SymphonieWizardConstants.DATA_EXPORTABLE);
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
    try {
      builder.setNameMap(newLanguage.getMap());
    } catch (IOException e) {
      errDisplay.showException(e);
    }
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

  // ----------------------------------------------------------------------------
  // Static fields
  // ----------------------------------------------------------------------------

  /** A transparent 16x16 icon */
  private static final ImageIcon EMPTYICON = new ImageIcon(Symphonie.class
      .getResource("icons/empty.png"));

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
    }/*
       * , RUSSIAN { }, JAPANESE { }
       */;

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

    /** Unicode charset */
    public static final String UNICODE = "Unicode";
  }

  /**
   * Enum represents program views <br>
   * A <code>View</code> can be exported and imported. <br>
   * In order to make abstraction of the export/import implemetations
   * <code>Views</code> use a visitor-like design pattern.
   */
  enum View {
    student {

      String getNameKey() {
        return VIEW_STUDENT_MENU_ITEM;
      }

      void exportView(DataExporter exporter, DataManager manager, Object data,
          String output) throws DataExporterException {
        exporter.exportStudentView(output, manager, (Student) data);
      }

      void importView(DataImporter importer, DataManager manager, Object input)
          throws DataImporterException {
        importer.importStudentView((String) input, manager);
      }
    },
    teacher {

      String getNameKey() {
        return VIEW_TEACHER_MENU_ITEM;
      }

      void exportView(DataExporter exporter, DataManager manager, Object data,
          String output) throws DataExporterException {
        exporter.exportTeacherView(output, manager, (Course) data);
      }

      void importView(DataImporter importer, DataManager manager, Object input)
          throws DataImporterException {
        importer.importTeacherView((String) input, manager);
      }
    },
    jury {

      String getNameKey() {
        return VIEW_JURY_MENU_ITEM;
      }

      void exportView(DataExporter exporter, DataManager manager, Object data,
          String output) throws DataExporterException {
        exporter.exportJuryView(output, manager);
      }

      void importView(DataImporter importer, DataManager manager, Object input)
          throws DataImporterException {
        importer.importJuryView((String) input, manager);
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
        Object data, String output) throws DataExporterException;

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
  }
}
