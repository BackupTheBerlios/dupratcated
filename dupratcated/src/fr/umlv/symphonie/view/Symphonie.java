/*
 * This file is part of Symphonie Created : 17 févr. 2005 21:30:59
 */

package fr.umlv.symphonie.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;

import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.data.SgainDataManager;
import fr.umlv.symphonie.data.Student;
import fr.umlv.symphonie.model.StudentModel;
import fr.umlv.symphonie.util.SymphonieComponentBuilder;
import fr.umlv.symphonie.util.TextualResourcesLoader;

public class Symphonie {

  private static final String FILE_PATTERN = "language/symphonie";
  private static final String FILE_CHARSET = "ISO-8859-1";

  // C'est mal
  private static SymphonieComponentBuilder builder;
  private static HashMap<String, String> english;
  private static HashMap<String, String> french;
  private static HashMap<String, String> spanish;

  private static JFrame getFrame() {

    try {
      english = TextualResourcesLoader.getResourceMap(FILE_PATTERN, new Locale(
          "english"), FILE_CHARSET);
      french = TextualResourcesLoader.getResourceMap(FILE_PATTERN, new Locale(
          "french"), FILE_CHARSET);
    } catch (IOException e) {
      throw new AssertionError("Bim ! -> " + e.getMessage());
    }

    builder = new SymphonieComponentBuilder(english);
    JFrame frame = new JFrame(builder.getValue(SymphonieConstants.FRAME_TITLE));
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

  private static JMenu getFileMenu() {
    JMenu file = builder.buildMenu(SymphonieConstants.FILE_MENU);

    /* Actions ******************************************************* */
    Action proxy = SymphonieActionFactory.getProxyAction(new ImageIcon(
        Symphonie.class.getResource("icons/proxy.png")));

    Action a_import = SymphonieActionFactory.getProxyAction(new ImageIcon(
        Symphonie.class.getResource("icons/import.png")));

    Action export = SymphonieActionFactory.getProxyAction(new ImageIcon(
        Symphonie.class.getResource("icons/export.png")));

    Action print = SymphonieActionFactory.getPrintAction(new ImageIcon(
        Symphonie.class.getResource("icons/print.png")));

    Action exit = SymphonieActionFactory.getExitAction(new ImageIcon(
        Symphonie.class.getResource("icons/exit.png")));

    /* Items********************************************************* */
    file.add(builder.buildMenuItem(proxy, SymphonieConstants.PROXY_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    file.add(builder.buildMenuItem(a_import,
        SymphonieConstants.IMPORT_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    file.add(builder.buildMenuItem(export, SymphonieConstants.EXPORT_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    file.add(builder.buildMenuItem(print, SymphonieConstants.PRINT_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    file.add(new JSeparator());
    file.add(builder.buildMenuItem(exit, SymphonieConstants.EXIT_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));

    return file;
  }

  private static JMenu getLangMenu() {
    JMenu lang = builder.buildMenu(SymphonieConstants.LANGUAGE_MENU_ITEM);

    // Actions
    Action inEnglish = SymphonieActionFactory.getLanguageChangeAction(english,
        builder);
    Action inFrench = SymphonieActionFactory.getLanguageChangeAction(french,
        builder);

    // Items
    JCheckBoxMenuItem engBox = (JCheckBoxMenuItem) builder.buildMenuItem(
        inEnglish, SymphonieConstants.ENGLISH_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.CHECK_BOX_ITEM);
    engBox.setSelected(true);
    JCheckBoxMenuItem frBox = (JCheckBoxMenuItem) builder.buildMenuItem(
        inFrench, SymphonieConstants.FRENCH_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.CHECK_BOX_ITEM);

    ButtonGroup g = new ButtonGroup();
    g.add(engBox);
    g.add(frBox);

    lang.add(engBox);
    lang.add(frBox);

    return lang;
  }

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

  private static JMenu getWindowMenu() {
    JMenu window = builder.buildMenu(SymphonieConstants.WINDOW_MENU);

    /* Items********************************************************* */
    window.add(getModeMenu());
    window.add(getLangMenu());

    return window;

  }

  private static JMenu getFormatMenu() {
    JMenu format = builder.buildMenu(SymphonieConstants.FORMAT_MENU);

    /* Actions ******************************************************* */
    Action formula = SymphonieActionFactory.getFormulaAction(new ImageIcon());
    Action f_cell = SymphonieActionFactory
        .getFormulaCellAction(new ImageIcon());

    /* Items********************************************************* */
    format.add(builder.buildMenuItem(formula,
        SymphonieConstants.FORMULA_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    format.add(builder.buildMenuItem(f_cell, SymphonieConstants.CELL_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));

    return format;
  }

  private static JMenu getInsertMenu() {
    JMenu insert = builder.buildMenu(SymphonieConstants.INSERT_MENU);
    /* Actions ******************************************************* */
    Action column = SymphonieActionFactory.getColumnAction(new ImageIcon());
    Action line = SymphonieActionFactory.getLineAction(new ImageIcon());

    /* Items********************************************************* */
    insert.add(builder.buildMenuItem(column,
        SymphonieConstants.INSERT_COLUMN_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    insert.add(builder.buildMenuItem(line,
        SymphonieConstants.INSERT_LINE_MENU_ITEM,
        SymphonieComponentBuilder.JMenuItemType.NORMAL_ITEM));
    return insert;
  }

  private static JMenu getAdminMenu() {
    JMenu admin = builder.buildMenu(SymphonieConstants.ADMIN_MENU);
    /* Actions ******************************************************* */
    Action connect = SymphonieActionFactory.getConnectAction(new ImageIcon(
        Symphonie.class.getResource("icons/admin.png")));
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

  private static JToolBar getToolbar() {
    JToolBar toolbar = new JToolBar();
    toolbar.add(SymphonieActionFactory.getConnectAction(new ImageIcon(
        Symphonie.class.getResource("icons/connect.png"))));
    toolbar.add(SymphonieActionFactory.getDBAction(new ImageIcon(
        Symphonie.class.getResource("icons/db.png"))));

    toolbar.setFloatable(false);

    return toolbar;
  }

  private static JPanel getContentPane(final JFrame f,
      final SymphonieComponentBuilder builder) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(getToolbar(), BorderLayout.NORTH);
    
    DataManager dataManager = new SgainDataManager();
    StudentModel model = new StudentModel(dataManager);

    Student student = new Student(1, "Fabien", "Vallee");

    

    model.setStudent(student);

    JTable table = new JTable(model);
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

    JScrollPane scroll = new JScrollPane(table);
    JTabbedPane tab = new JTabbedPane(JTabbedPane.BOTTOM,
        JTabbedPane.SCROLL_TAB_LAYOUT);
    tab.add(SymphonieConstants.VIEW_STUDENT_MENU_ITEM, scroll);
    panel.add(tab);
    builder.addChangeListener(panel, new ChangeListener() {

      public void stateChanged(ChangeEvent arg0) {
        f.setTitle(builder.getValue(SymphonieConstants.FRAME_TITLE));
      }
    });

    return panel;
  }

  public static void main(String[] args) {
    JFrame f = getFrame();
    f.pack();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
  }
}
