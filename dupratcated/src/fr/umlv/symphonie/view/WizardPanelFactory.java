/*
 * This file is part of Symphonie
 * Created : 1 mars 2005 23:46:07
 */

package fr.umlv.symphonie.view;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import fr.umlv.symphonie.data.DataManager;
import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.ExceptionDisplayDialog;
import fr.umlv.symphonie.util.ComponentBuilder.ButtonType;
import fr.umlv.symphonie.util.dataexport.DataExporter;
import fr.umlv.symphonie.util.dataexport.hssf.HSSFDataExporter;
import fr.umlv.symphonie.util.dataexport.xml.XMLExporter;
import fr.umlv.symphonie.util.dataimport.DataImporter;
import fr.umlv.symphonie.util.dataimport.xml.XMLImporter;
import fr.umlv.symphonie.util.wizard.DefaultWizardModel;
import fr.umlv.symphonie.util.wizard.Wizard;
import fr.umlv.symphonie.util.wizard.WizardModel;
import fr.umlv.symphonie.util.wizard.WizardPanel;
import fr.umlv.symphonie.util.wizard.event.WizardEvent;
import fr.umlv.symphonie.util.wizard.event.WizardListenerAdapter;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.DATA_EXPORTER;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.DATA_EXPORT_TYPE;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.DATA_EX_DIALOG;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.DATA_FILE;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.DATA_IMPORTER;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.DATA_MANAGER;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.DATA_VIEW;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.EWIZARD_DEST_FILE;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.EWIZARD_DEST_FILE_TITLE;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.EWIZARD_FORMAT;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.EWIZARD_FORMAT_SELECTION;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.EWIZARD_HELP;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.EWIZARD_READY;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.EWIZARD_READY_HELP1;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.EWIZARD_READY_HELP2;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.EWIZARD_TITLE;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.IWIZARD_HELP;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.IWIZARD_READY;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.IWIZARD_READY_HELP1;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.IWIZARD_READY_HELP2;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.IWIZARD_SRC_FILE;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.IWIZARD_SRC_FILE_TITLE;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.IWIZARD_TITLE;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.WIZARD_BROWSE;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.WIZARD_CANCEL;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.WIZARD_EXPORT;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.WIZARD_FINISH;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.WIZARD_HELP;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.WIZARD_IMPORT;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.WIZARD_NEXT;
import static fr.umlv.symphonie.view.SymphonieWizardConstants.WIZARD_PREVIOUS;
import fr.umlv.symphonie.view.Symphonie.View;

/**
 * Factory of panels used in <code>Symphonie</code> import/export wizards.
 * 
 * @author PEÑA SALDARRIAGA Sébastian
 */
public class WizardPanelFactory {

  /** Icon for XML import/export type */
  public static final ImageIcon XML_TYPE = new ImageIcon(
      WizardPanelFactory.class.getResource("icons/xml.png"));

  /** Icon for HSSF/Excel import/export type */
  public static final ImageIcon HSSF_TYPE = new ImageIcon(
      WizardPanelFactory.class.getResource("icons/hssf.png"));

  /** XML description key */
  public static final String XML_DESC = "importexport.exml";

  /** HSSF description key */
  public static final String HSSF_DESC = "importexport.ehssf";

  /** File filter for XML files */
  public static final FileFilter XML_FILTER = new FileFilter() {

    public boolean accept(File path) {
      return path.getPath().endsWith(".xml");
    }

    public String getDescription() {
      return "XML Files";
    }
  };

  /** File filter for HSSF/Excel files */
  public static final FileFilter HSSF_FILTER = new FileFilter() {

    public boolean accept(File path) {
      return path.getPath().endsWith(".xls");
    }

    public String getDescription() {
      return "Excel 97 Horrible Files";
    }
  };

  /** Import and Export formats */
  enum ImportExportFormats {
    XML {

      String getExtension() {
        return "xml";
      }

      Icon getIcon() {
        return XML_TYPE;
      }

      String getDescription() {
        return XML_DESC;
      }

      FileFilter getFileFilter() {
        return XML_FILTER;
      }

      private XMLExporter exporter = new XMLExporter();

      DataExporter getExporter(Object o) {
        return exporter;
      }

      private XMLImporter importer = new XMLImporter();

      DataImporter getImporter(Object o) {
        return importer;
      }
    },
    HSSF {

      String getExtension() {
        return "xls";
      }

      Icon getIcon() {
        return HSSF_TYPE;
      }

      String getDescription() {
        return HSSF_DESC;
      }

      FileFilter getFileFilter() {
        return HSSF_FILTER;
      }

      DataExporter getExporter(Object o) {
        return HSSFDataExporter.getSingletonInstance((ComponentBuilder) o);
      }

      DataImporter getImporter(Object o) {
        return null;
      }
    };

    /**
     * Provides the file extension of the type
     * 
     * @return The file extension for the i/e type
     */
    abstract String getExtension();

    /**
     * Provides an icon for the type
     * 
     * @return An icon for the i/e type
     */
    abstract Icon getIcon();

    /**
     * Provides a human readable description of the type
     * 
     * @return The description of the i/e type
     */
    abstract String getDescription();

    /**
     * Provides a file filter for the type
     * 
     * @return A file filter usable in a <code>JFileChooser</code>
     */
    abstract FileFilter getFileFilter();

    /**
     * Provides an exporter for the type, for some types it may return null
     * 
     * @param o
     *          Some data
     * @return a <code>DataExporter</code>
     */
    abstract DataExporter getExporter(Object o);

    /**
     * Provides an importer for the type, for some types it may return null
     * 
     * @param o
     *          Some data
     * @return a <code>DataImporter</code>
     */
    abstract DataImporter getImporter(Object o);
  }

  /**
   * Makes a displayable JList of all the import/export types.
   * 
   * @param b
   *          a <code>ComponentBuilder</code> for the internationalization
   * @return a <code>JList</code>
   */
  public static final JList getFormatList(final ComponentBuilder b) {
    final JList formatList = new JList(ImportExportFormats.values());
    formatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    formatList.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    formatList.setCellRenderer(new DefaultListCellRenderer() {

      public Component getListCellRendererComponent(JList list, Object value,
          int index, boolean isSelected, boolean cellHasFocus) {
        JLabel l = (JLabel) super.getListCellRendererComponent(list, value,
            index, isSelected, cellHasFocus);
        ImportExportFormats f = (ImportExportFormats) value;
        l.setText(b.getValue(f.getDescription()) + " (*." + f.getExtension()
            + ")");
        l.setIcon(f.getIcon());
        return l;
      }
    });
    return formatList;
  }

  /**
   * Provides a <code>JPanel</code> for browsing files.
   * 
   * @param wp
   *          the <code>WizardPanel</code> where the component will be used.
   *          The selected file will be put in the wizard panel data map as
   *          "destFile".
   * @param p
   *          the panel to build
   * @param fileChooser
   *          the <code>JFileChooser</code> for browsing
   * @param b
   *          a <code>ComponentBuilder</code> for the internationalization
   * @param labelKey
   *          the key for the label
   * @param hintKey
   *          the for the user help
   */
  private static final void makeBrowsePanel(final WizardPanel wp,
      final JPanel p, final JFileChooser fileChooser, final ComponentBuilder b,
      final String labelKey, final String hintKey) {
    final JTextField field = new JTextField();
    field.addKeyListener(new KeyAdapter() {

      public void keyPressed(KeyEvent e) {
        String fname = field.getText();
        wp.getData().put(DATA_FILE,
            ((fname == null) || (fname.equals(""))) ? null : new File(fname));
        wp.firePanelChanged();
      }
    });
    AbstractAction a = new AbstractAction("Browse...") {

      public void actionPerformed(ActionEvent event) {
        int r = fileChooser.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
          File f = fileChooser.getSelectedFile();
          field.setText(f.getPath());
          wp.getData().put(DATA_FILE, f);
          wp.firePanelChanged();
        }
      }
    };

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 50, 5, 5);
    gbc.anchor = GridBagConstraints.WEST;
    p.add(b.buildLabel(labelKey), gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.insets.left = 5;
    gbc.insets.right = 50;
    p.add(b.buildButton(a, WIZARD_BROWSE, ButtonType.BUTTON), gbc);
    gbc.weightx = 1.0;
    gbc.insets.left = 50;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    p.add(field, gbc);
    gbc.insets.top = 35;
    gbc.insets.left = gbc.insets.right = 70;
    final JTextArea ta = new JTextArea(b.getValue(hintKey));
    ta.setBackground(p.getBackground());
    ta.setBorder(null);
    ta.setEditable(false);
    ta.setLineWrap(true);
    ta.setWrapStyleWord(true);
    b.addChangeListener(ta, new ChangeListener() {

      public void stateChanged(ChangeEvent ev) {
        ta.setEditable(true);
        ta.setText(b.getValue(hintKey));
        ta.setEditable(false);
      }
    });
    p.add(ta, gbc);
  }

  /**
   * Makes a <code>WizardModel</code> with internationalization support.
   * 
   * @param b
   *          a <code>ComponentBuilder</code> for internationalization
   * @param titleKey
   *          a key for the model's title
   * @param icon
   *          an <code>Icon</code> for the wizard
   * @return an international model
   */
  public static final WizardModel getInternationalWizardModel(
      final ComponentBuilder b, final String titleKey, final Icon icon) {
    return new DefaultWizardModel() {

      public String getCancelButtonText() {
        return b.getValue(WIZARD_CANCEL);
      }

      public String getFinishButtonText() {
        return b.getValue(WIZARD_FINISH);
      }

      public String getHelpButtonText() {
        return b.getValue(WIZARD_HELP);
      }

      public String getNextButtonText() {
        return b.getValue(WIZARD_NEXT);
      }

      public String getPreviousButtonText() {
        return b.getValue(WIZARD_PREVIOUS);
      }

      public String getWizardTitle() {
        return b.getValue(titleKey);
      }

      public Icon getWizardIcon() {
        return icon;
      }
    };
  }

  /* ----------------- Export Wizard Panels ----------------- */

  /**
   * Gets the format selection wizard panel for the export wizard
   * 
   * @param b
   *          a <code>ComponentBuilder</code> for internationalization
   * @return a <code>WizardPanel</code>
   */
  public static WizardPanel getExportFormatSelectionPanel(
      final ComponentBuilder b) {
    final JPanel p = new JPanel(new GridBagLayout());
    final JList list = WizardPanelFactory.getFormatList(b);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    gbc.insets.left = 30;
    gbc.insets.right = 30;
    gbc.insets.top = 20;
    JLabel lab = b.buildLabel(EWIZARD_FORMAT_SELECTION);
    lab.setFont(lab.getFont().deriveFont(Font.PLAIN));
    p.add(lab, gbc);
    gbc.insets.top = 5;
    gbc.insets.bottom = 30;
    gbc.gridheight = GridBagConstraints.REMAINDER;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1.0;
    gbc.weighty = 1.0;
    p.add(list, gbc);
    final WizardPanel pan = new WizardPanel() {

      public String getTitle() {
        return b.getValue(EWIZARD_TITLE);
      }

      public String getDescription() {
        return b.getValue(EWIZARD_FORMAT);
      }

      public JComponent getPanelComponent() {
        return p;
      }

      public boolean isValid() {
        return list.getSelectedValue() != null;
      }

      public boolean canFinish() {
        return false;
      }
    };

    list.addListSelectionListener(new ListSelectionListener() {

      public void valueChanged(ListSelectionEvent event) {
        ImportExportFormats f = (ImportExportFormats) list.getSelectedValue();
        pan.getData().put(DATA_EXPORT_TYPE, f);
        pan.getData().put(DATA_EXPORTER, f.getExporter(b));
        pan.firePanelChanged();
      }
    });

    return pan;
  }

  /**
   * Gets the file selection wizard panel for the export wizard
   * 
   * @param b
   *          a <code>ComponentBuilder</code> for internationalization
   * @return a <code>WizardPanel</code>
   */
  public static final WizardPanel getExportFileSelectionPanel(
      final ComponentBuilder b) {
    final WizardPanel wp = new WizardPanel() {

      private JPanel p;
      private final JFileChooser fileChooser = new JFileChooser();

      public String getTitle() {
        return b.getValue(EWIZARD_TITLE);
      }

      public String getDescription() {
        return b.getValue(EWIZARD_DEST_FILE_TITLE);
      }

      public JComponent getPanelComponent() {
        if (p == null) {
          p = new JPanel(new GridBagLayout());
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
          fileChooser.setMultiSelectionEnabled(false);
          makeBrowsePanel(this, p, fileChooser, b, EWIZARD_DEST_FILE,
              EWIZARD_HELP);
        }
        fileChooser.resetChoosableFileFilters();
        fileChooser.setFileFilter(((ImportExportFormats) data
            .get(DATA_EXPORT_TYPE)).getFileFilter());
        return p;
      }

      public boolean isValid() {
        File f = (File) data.get(DATA_FILE);
        return (f != null) && (f.exists() ? f.canWrite() : true);
      }

      public boolean canFinish() {
        return false;
      }
    };
    return wp;
  }

  /**
   * Gets the last panel for the export wizard
   * 
   * @param wiz
   *          The wizard that will display the panel
   * @param b
   *          The builder for internationalization
   * @return a <code>WizardPanel</code>
   */
  public static final WizardPanel getExportFinishPanel(final Wizard wiz,
      final ComponentBuilder b) {
    final JPanel p = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(20, 50, 0, 50);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    p.add(b.buildLabel(EWIZARD_READY_HELP1), gbc);
    gbc.insets.top = 10;
    p.add(b.buildLabel(EWIZARD_READY_HELP2), gbc);
    gbc.insets.top = 30;
    gbc.insets.bottom = 30;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    final JProgressBar prog = new JProgressBar(0, 100);
    prog.setValue(0);
    p.add(prog, gbc);
    gbc.insets.top = 0;
    gbc.fill = GridBagConstraints.NONE;
    final JButton fin = (JButton) b.buildButton(WIZARD_EXPORT,
        ButtonType.BUTTON);
    p.add(fin, gbc);

    final WizardPanel wp = new WizardPanel() {

      public String getTitle() {
        return b.getValue(EWIZARD_TITLE);
      }

      public String getDescription() {
        return b.getValue(EWIZARD_READY);
      }

      public JComponent getPanelComponent() {
        return p;
      }

      public boolean isValid() {
        return true;
      }

      public boolean canFinish() {
        return true;
      }
    };

    fin.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        wiz.disableAllButtons();
        fin.setEnabled(false);
        final Map<Object, Object> data = wp.getData();
        final ExceptionDisplayDialog dialog = (ExceptionDisplayDialog) data
            .get(DATA_EX_DIALOG);
        final DataManager dm = (DataManager) data.get(DATA_MANAGER);
        final DataExporter exp = (DataExporter) data.get(DATA_EXPORTER);
        final String file = ((File) data.get(DATA_FILE)).getAbsolutePath();
        prog.setValue(15);
        new Thread() {

          public void run() {
            try {
              Symphonie.View view = (View) data.get(DATA_VIEW);

              view.exportView(exp, dm, data, file);
            } catch (Throwable t) {
              if (dialog != null)
                dialog.showException(t);
              else
                t.printStackTrace();
            }
            prog.setValue(100);
            wiz.setEnabledFinish(true);
          }
        }.start();
      }
    });

    wiz.getModel().addWizardListener(new WizardListenerAdapter() {

      public void wizardFinished(WizardEvent we) {
        prog.setValue(0);
        fin.setEnabled(true);
      }
    });
    return wp;
  }

  /* ----------------- Import Wizard Panels ----------------- */

  /**
   * Returns the file selection panel for the import wizard
   * 
   * @param b
   *          The builder for internationalization
   * @return a <code>WizardPanel</code>
   */
  public static final WizardPanel getImportFileSelectionPanel(
      final ComponentBuilder b) {
    final WizardPanel wp = new WizardPanel() {

      private JPanel p;
      private final JFileChooser fileChooser = new JFileChooser();

      public String getTitle() {
        return b.getValue(IWIZARD_TITLE);
      }

      public String getDescription() {
        return b.getValue(IWIZARD_SRC_FILE_TITLE);
      }

      public JComponent getPanelComponent() {
        if (p == null) {
          p = new JPanel(new GridBagLayout());
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
          fileChooser.setMultiSelectionEnabled(false);
          fileChooser.setFileFilter(ImportExportFormats.XML.getFileFilter());
          makeBrowsePanel(this, p, fileChooser, b, IWIZARD_SRC_FILE,
              IWIZARD_HELP);
          data.put(DATA_IMPORTER, ImportExportFormats.XML.getImporter(null));
        }
        return p;
      }

      public boolean isValid() {
        File f = (File) data.get(SymphonieWizardConstants.DATA_FILE);
        return (f != null) && f.exists() && f.canRead();
      }

      public boolean canFinish() {
        return false;
      }
    };
    return wp;
  }

  /**
   * Gets the last panel for the import wizard
   * 
   * @param wiz
   *          The wizard that will display the panel
   * @param b
   *          The builder for internationalization
   * @return a <code>WizardPanel</code>
   */
  public static WizardPanel getImportPanel(final Wizard wiz,
      final ComponentBuilder b) {

    final JPanel p = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(20, 50, 0, 50);
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    p.add(b.buildLabel(IWIZARD_READY_HELP1), gbc);
    gbc.insets.top = 10;
    p.add(b.buildLabel(IWIZARD_READY_HELP2), gbc);
    gbc.insets.top = 30;
    gbc.insets.bottom = 30;
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    final JProgressBar prog = new JProgressBar(0, 100);
    prog.setValue(0);
    p.add(prog, gbc);
    gbc.insets.top = 0;
    gbc.fill = GridBagConstraints.NONE;
    final JButton fin = (JButton) b.buildButton(WIZARD_IMPORT,
        ButtonType.BUTTON);
    p.add(fin, gbc);

    final WizardPanel wp = new WizardPanel() {

      public String getTitle() {
        return b.getValue(IWIZARD_TITLE);
      }

      public String getDescription() {
        return b.getValue(IWIZARD_READY);
      }

      public JComponent getPanelComponent() {
        return p;
      }

      public boolean isValid() {
        return true;
      }

      public boolean canFinish() {
        return true;
      }
    };

    fin.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        wiz.disableAllButtons();
        fin.setEnabled(false);
        final Map<Object, Object> data = wp.getData();
        final ExceptionDisplayDialog dialog = (ExceptionDisplayDialog) data
            .get(DATA_EX_DIALOG);
        final DataManager dm = (DataManager) data.get(DATA_MANAGER);
        final String file = ((File) data.get(DATA_FILE)).getAbsolutePath();
        final DataImporter imp = (DataImporter) data.get(DATA_IMPORTER);
        prog.setValue(15);
        new Thread() {

          public void run() {
            try {
              Symphonie.View view = (View) data.get(DATA_VIEW);
              view.importView(imp, dm, file);
            } catch (Throwable t) {
              if (dialog != null)
                dialog.showException(t);
              else
                t.printStackTrace();
            }
            prog.setValue(100);
            wiz.setEnabledFinish(true);
          }
        }.start();
      }
    });

    wiz.getModel().addWizardListener(new WizardListenerAdapter() {

      public void wizardFinished(WizardEvent we) {
        prog.setValue(0);
        fin.setEnabled(true);
      }
    });
    return wp;
  }
}
