/*
 * This file is part of Symphonie
 * Created : 19 mars 2005 09:21:21
 */

package fr.umlv.symphonie.view;

import javax.swing.ImageIcon;

/**
 * Contains constants used in wizards
 */
public final class SymphonieWizardConstants {

  /**
   * Sole constructor
   */
  private SymphonieWizardConstants() {
  }

  // ----------------------------------------------------------------------------
  // Icons
  // ----------------------------------------------------------------------------

  /** Default icon for export wizard */
  public static final ImageIcon EXPORT_WIZARD_ICON = new ImageIcon(
      SymphonieWizardConstants.class.getResource("icons/export_wiz.gif"));

  /** Default icon for export wizard */
  public static final ImageIcon IMPORT_WIZARD_ICON = new ImageIcon(
      SymphonieWizardConstants.class.getResource("icons/import_wiz.gif"));

  // ----------------------------------------------------------------------------
  // Wizard buttons
  // ---------------------------------------------------------------------------

  public static final String WIZARD_CANCEL = "importexport.cancelButton";
  public static final String WIZARD_FINISH = "importexport.finishButton";
  public static final String WIZARD_HELP = "importexport.helpButton";
  public static final String WIZARD_NEXT = "importexport.nextButton";
  public static final String WIZARD_PREVIOUS = "importexport.prevButton";
  public static final String WIZARD_BROWSE = "importexport.browse";
  public static final String WIZARD_EXPORT = "importexport.export";
  public static final String WIZARD_IMPORT = "importexport.import";

  // ----------------------------------------------------------------------------
  // Export wizard texts
  // ---------------------------------------------------------------------------

  public static final String EWIZARD_TITLE = "importexport.ewizard";
  public static final String EWIZARD_FORMAT = "importexport.eformat";
  public static final String EWIZARD_FORMAT_SELECTION = "importexport.eselectformat";
  public static final String EWIZARD_XML = "importexport.exml";
  public static final String EWIZARD_HSSF = "importexport.ehssf";
  public static final String EWIZARD_DEST_FILE_TITLE = "importexport.efile";
  public static final String EWIZARD_HELP = "importexport.ehelp";
  public static final String EWIZARD_DEST_FILE = "importexport.edestfile";
  public static final String EWIZARD_READY = "importexport.ereadytogo";
  public static final String EWIZARD_READY_HELP1 = "importexport.ereadyhelp1";
  public static final String EWIZARD_READY_HELP2 = "importexport.ereadyhelp2";

  // ----------------------------------------------------------------------------
  // Import wizard texts
  // ---------------------------------------------------------------------------
  
  public static final String IWIZARD_TITLE = "importexport.iwizard";
  public static final String IWIZARD_SRC_FILE_TITLE = "importexport.ifile";
  public static final String IWIZARD_SRC_FILE = "importexport.isrcfile";
  public static final String IWIZARD_HELP = "importexport.ihelp";
  public static final String IWIZARD_READY = "importexport.iready";
  public static final String IWIZARD_READY_HELP1 = "importexport.ireadyhelp1";
  public static final String IWIZARD_READY_HELP2 = "importexport.ireadyhelp2";
  
  
  // ----------------------------------------------------------------------------
  // Internal data
  // ---------------------------------------------------------------------------
  
  public static final String DATA_FILE = "file";
  public static final String DATA_EXPORT_TYPE = "exportType";
  public static final String DATA_EXPORTABLE = "exportableData";
  public static final String DATA_IMPORTABLE = "importableData";
  public static final String DATA_EX_DIALOG = "exceptionDialog";
  public static final String DATA_EXPORTER = "exporter";
  public static final String DATA_IMPORTER = "importer";
  public static final String DATA_MANAGER = "dataManager";
}
