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
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import fr.umlv.symphonie.util.ComponentBuilder;
import fr.umlv.symphonie.util.wizard.DefaultWizardModel;
import fr.umlv.symphonie.util.wizard.WizardModel;
import fr.umlv.symphonie.util.wizard.WizardPanel;
import fr.umlv.symphonie.util.wizard.event.WizardEvent;
import fr.umlv.symphonie.util.wizard.event.WizardListenerAdapter;

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
    AbstractAction a = new AbstractAction("Browse...") {

      public void actionPerformed(ActionEvent event) {
        int r = fileChooser.showSaveDialog(null);
        if (r == JFileChooser.APPROVE_OPTION) {
          File f = fileChooser.getSelectedFile();
          field.setText(f.getPath());
          System.out.println(field.getText());
          wp.getData().put("destFile", f);
          wp.firePanelChanged();
        }
      }
    };

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 50, 5, 50);
    gbc.anchor = GridBagConstraints.WEST;
    p.add(b.buildLabel(labelKey), gbc);
    gbc.anchor = GridBagConstraints.EAST;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    p.add(b.buildButton(a, "importexport.browse",
        ComponentBuilder.ButtonType.BUTTON), gbc);
    gbc.weightx = 1.0;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.gridwidth = GridBagConstraints.REMAINDER;
    p.add(field, gbc);
    gbc.insets.top = 35;
    gbc.insets.left = gbc.insets.right = 70;
    final JTextArea ta = new JTextArea(b.getValue(hintKey));
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
   * @return an international model
   */
  public static final WizardModel getInternationalWizardModel(
      final ComponentBuilder b, final String titleKey) {
    return new DefaultWizardModel() {

      public String getCancelButtonText() {
        return b.getValue("importexport.cancelButton");
      }

      public String getFinishButtonText() {
        return b.getValue("importexport.finishButton");
      }

      public String getHelpButtonText() {
        return b.getValue("importexport.helpButton");
      }

      public String getNextButtonText() {
        return b.getValue("importexport.nextButton");
      }

      public String getPreviousButtonText() {
        return b.getValue("importexport.prevButton");
      }

      public String getWizardTitle() {
        return b.getValue(titleKey);
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
    JLabel lab = b.buildLabel("importexport.eselectformat");
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
        return b.getValue("importexport.ewizard");
      }

      public String getDescription() {
        return b.getValue("importexport.eformat");
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
        pan.getData().put("exportType", list.getSelectedValue());
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
        return b.getValue("importexport.ewizard");
      }

      public String getDescription() {
        return b.getValue("importexport.efile");
      }

      public JComponent getPanelComponent() {
        if (p == null) {
          p = new JPanel(new GridBagLayout());
          fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
          fileChooser.setMultiSelectionEnabled(false);
          makeBrowsePanel(this, p, fileChooser, b, "importexport.edestfile",
              "importexport.ehelp");
        }
        fileChooser.resetChoosableFileFilters();
        fileChooser
            .setFileFilter(((ImportExportFormats) data.get("exportType"))
                .getFileFilter());
        return p;
      }

      public boolean isValid() {
        File f = (File) data.get("destFile");
        return (f != null) && (f.exists() ? f.canWrite() : true);
      }

      public boolean canFinish() {
        return false;
      }
    };
    return wp;
  }

  /**
   * Gets the last wizard panel for the export wizard
   * 
   * @param model
   *          The model where the panel will be contained. The wizard panel
   *          created needs to be registered <b>as the last listener </b> to the
   *          model.
   * @param b
   *          a <code>ComponentBuilder</code> for internationalization
   * @return a <code>WizardPanel</code>
   */
  public static final WizardPanel getExportFinishPanel(final WizardModel model,
      final ComponentBuilder b) {
    final JPanel p = new JPanel();
    p.add(b.buildLabel("importexport.ereadyhelp"));
    p.add(new JLabel(" PAS ENCORE FINI !"));
    final WizardPanel wp = new WizardPanel() {

      public String getTitle() {
        return b.getValue("importexport.ewizard");
      }

      public String getDescription() {
        return b.getValue("importexport.ereadytogo");
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
    model.addWizardListener(new WizardListenerAdapter() {

      public void wizardFinished(WizardEvent we) {
        // TODO
        // Le code qui lance l'export
      }
    });
    return wp;
  }

  /* ----------------- Import Wizard Panels ----------------- */

  // TODO
  public static WizardPanel getImportPanel() {
    return new WizardPanel() {

      public String getTitle() {
        // TODO Auto-generated method stub
        return "TODO Auto-generated method stub";
      }

      public String getDescription() {
        // TODO Auto-generated method stub
        return "TODO Auto-generated method stub";
      }

      public JComponent getPanelComponent() {
        // TODO Auto-generated method stub
        return new JLabel("TODO Auto-generated method stub");
      }

      public boolean isValid() {
        // TODO Auto-generated method stub
        return false;
      }

      public boolean canFinish() {
        // TODO Auto-generated method stub
        return false;
      }
    };
  }
}
