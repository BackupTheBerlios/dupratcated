/*
 * This file is part of Symphonie
 * Created : 10 mars 2005 00:52:16
 */

package fr.umlv.symphonie.view.dialog;

import java.awt.GridBagConstraints;
import static java.awt.GridBagConstraints.CENTER;
import static java.awt.GridBagConstraints.EAST;
import static java.awt.GridBagConstraints.HORIZONTAL;
import static java.awt.GridBagConstraints.NONE;
import static java.awt.GridBagConstraints.RELATIVE;
import static java.awt.GridBagConstraints.REMAINDER;
import static java.awt.GridBagConstraints.WEST;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyListener;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.text.JTextComponent;

import fr.umlv.symphonie.util.SymphoniePreferencesManager;
import static fr.umlv.symphonie.util.SymphoniePreferencesManager.DB_HOST;
import static fr.umlv.symphonie.util.SymphoniePreferencesManager.DB_NAME;
import static fr.umlv.symphonie.util.SymphoniePreferencesManager.DB_PASS;
import static fr.umlv.symphonie.util.SymphoniePreferencesManager.DB_PORT;
import static fr.umlv.symphonie.util.SymphoniePreferencesManager.DB_USER;
import fr.umlv.symphonie.util.SymphoniePreferencesManager.DataBaseType;

/**
 * Dialog for gathering database connection information. <br>
 * Dialog is in plain english, it's not internationalizable and it will never
 * be.
 */
public class DatabaseDialog {

  /** Internal dialog component */
  private final JDialog editor;

  /** User input values */
  private final Properties userInput = new Properties();

  /**
   * Creates a new DatabaseDialog
   * 
   * @param owner
   *          The dialog owner
   */
  public DatabaseDialog(JFrame owner) {
    editor = new JDialog(owner, true);
    editor.setTitle("Database Information");
    editor.setSize(370, 300);
    editor.setContentPane(makeContentPane());
    editor.setLocationRelativeTo(owner);
    editor.setResizable(false);
  }

  /**
   * Creates the dialog content pane
   * 
   * @return a JPanel
   */
  private JPanel makeContentPane() {

    JPanel userPassButtons = new JPanel(new GridBagLayout());

    // Database information panel
    final JTextField server = new JTextField("");
    final JTextField port = new JTextField("");
    final JTextField base = new JTextField("");
    JPanel dbInfo = getDBInfoPanel(server, port, base);

    final JComboBox typeBox = new JComboBox(DataBaseType.values());
    typeBox.setSelectedItem(DataBaseType.PostgreSQL);
    final JTextField user = new JTextField("");
    final JPasswordField password = new JPasswordField("");
    JPanel dbUser = getDBUsernamePasswordPanel(user, password, typeBox);

    JButton bok = new JButton(new AbstractAction("Accept") {

      public void actionPerformed(ActionEvent e) {

        String porc = port.getText();
        if (!porc.equals("")) {
          try {
            int port = Integer.parseInt(porc);
            userInput.setProperty(DB_PORT, porc);
          } catch (NumberFormatException e1) {
            JOptionPane
                .showMessageDialog(editor,
                    "Invalid port number. Please type a port number, or left the field empty");
            return;
          }
        }
        DataBaseType type = (DataBaseType) typeBox.getSelectedItem();
        SymphoniePreferencesManager.setDBType(type);
        StringBuilder url = new StringBuilder("jdbc:");
        url.append(type.getSubprotcol()).append("://");
        url.append(server.getText());
        if (!(porc == null || porc.equals(""))) url.append(':').append(porc);
        url.append('/').append(base.getText());

        String pass = new String(password.getPassword());
        userInput.setProperty(DB_NAME, base.getText());
        userInput.setProperty(DB_HOST, server.getText());
        userInput.setProperty(DB_USER, user.getText());
        userInput.setProperty(DB_PASS, pass);
        SymphoniePreferencesManager.setDBProperties(userInput);
        userInput.clear();

        userInput.setProperty("user", user.getText());
        userInput.setProperty("password", pass);
        userInput.setProperty("url", url.toString());

        editor.setVisible(false);
        password.setText("");
        user.setText("");
        base.setText("");
        port.setText("");
      }
    });
    bok.setEnabled(false);

    JButton bcancel = new JButton(new AbstractAction("Cancel") {

      public void actionPerformed(ActionEvent e) {
        setVisible(false);
        password.setText("");
        user.setText("");
        base.setText("");
        port.setText("");
      }
    });

    KeyListener kl = getAcceptButtonEnabler(bok, server, base, user);
    server.addKeyListener(kl);
    base.addKeyListener(kl);
    user.addKeyListener(kl);
    password.addKeyListener(kl);

    JPanel allButButt = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.fill = HORIZONTAL;
    gbc.gridwidth = REMAINDER;
    gbc.weightx = 1.0;
    allButButt.add(dbInfo, gbc);
    allButButt.add(dbUser, gbc);
    allButButt
        .setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

    JPanel global = new JPanel(new GridBagLayout());
    gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 5, 5, 5);
    gbc.fill = HORIZONTAL;
    gbc.gridwidth = REMAINDER;
    gbc.weightx = 1.0;
    global.add(allButButt, gbc);
    gbc.insets.right = 20;
    gbc.insets.left = 40;
    gbc.fill = NONE;
    gbc.gridwidth = RELATIVE;
    gbc.anchor = EAST;
    global.add(bok, gbc);
    gbc.insets.right = 40;
    gbc.insets.left = 20;
    gbc.anchor = WEST;
    gbc.gridwidth = REMAINDER;
    global.add(bcancel, gbc);
    return global;
  }

  /**
   * Makes the username/password panel
   * 
   * @param user
   *          The username field
   * @param password
   *          The password fiel
   * @param typeBox
   *          The database type combo box
   * @return a <code>JPanel</code>
   */
  private JPanel getDBUsernamePasswordPanel(JTextField user,
      JPasswordField password, JComboBox typeBox) {
    JPanel p = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets.top = 10;
    gbc.gridwidth = RELATIVE;
    gbc.anchor = WEST;
    gbc.fill = HORIZONTAL;
    gbc.weightx = 1.0;
    gbc.insets.left = 20;
    p.add(new JLabel("Username :"), gbc);

    gbc.gridwidth = REMAINDER;
    gbc.anchor = EAST;
    gbc.insets.right = 20;
    gbc.insets.left = 0;
    gbc.weightx = 10.0;
    p.add(new JLabel("Password :"), gbc);

    gbc.insets.top = 5;
    gbc.fill = HORIZONTAL;
    gbc.anchor = WEST;
    gbc.gridwidth = RELATIVE;
    gbc.insets.left = 20;
    p.add(user, gbc);

    gbc.weightx = 1.0;
    gbc.anchor = EAST;
    gbc.fill = HORIZONTAL;
    gbc.gridwidth = REMAINDER;
    gbc.insets.right = 20;
    gbc.insets.left = 0;
    p.add(password, gbc);

    gbc.insets.top = 5;
    gbc.fill = HORIZONTAL;
    gbc.anchor = WEST;
    gbc.gridwidth = RELATIVE;
    gbc.insets.left = 20;
    p.add(new JLabel("Database type :"), gbc);

    gbc.weightx = 1.0;
    gbc.fill = NONE;
    gbc.gridwidth = REMAINDER;
    gbc.insets.right = 20;
    gbc.insets.left = 0;
    gbc.insets.top = gbc.insets.bottom = 10;
    p.add(typeBox, gbc);
    return p;
  }

  /**
   * Makes the DB information panel
   * 
   * @param server
   *          Server field
   * @param port
   *          Port field
   * @param base
   *          DB name fiel
   * @return A <code>JPanel</code>
   */
  private final JPanel getDBInfoPanel(JTextField server, JTextField port,
      JTextField base) {
    JPanel dbInfo = new JPanel(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets.top = 5;
    gbc.anchor = WEST;
    gbc.fill = HORIZONTAL;
    gbc.weightx = 10.0;
    gbc.insets.left = 20;
    dbInfo.add(new JLabel("Server :"), gbc);

    gbc.gridwidth = REMAINDER;
    gbc.anchor = WEST;
    gbc.fill = NONE;
    gbc.insets.right = 20;
    gbc.insets.left = 0;
    gbc.weightx = 0.0;
    dbInfo.add(new JLabel("Port :"), gbc);

    gbc.insets.top = 5;
    gbc.weightx = 10.0;
    gbc.anchor = WEST;
    gbc.fill = HORIZONTAL;
    gbc.gridwidth = RELATIVE;
    gbc.insets.left = 20;
    dbInfo.add(server, gbc);

    gbc.weightx = 1.0;
    gbc.anchor = EAST;
    gbc.fill = HORIZONTAL;
    gbc.gridwidth = REMAINDER;
    gbc.insets.right = 20;
    gbc.insets.left = 0;
    gbc.weightx = 0.0;
    port.setColumns(4);
    dbInfo.add(port, gbc);

    gbc.insets.top = 10;
    gbc.anchor = CENTER;
    gbc.fill = HORIZONTAL;
    gbc.gridwidth = REMAINDER;
    gbc.insets.right = 20;
    gbc.insets.left = 20;
    dbInfo.add(new JLabel("Remote database name :"), gbc);

    gbc.insets.top = 5;
    dbInfo.add(base, gbc);
    return dbInfo;
  }

  /**
   * Sets dialog as modal
   * 
   * @param modal
   *          <code>true</code> or <code>false</code>
   */
  public void setModal(boolean modal) {
    editor.setModal(modal);
  }

  /**
   * Displays/hides dialog. Calling this method clears user input
   * 
   * @param show
   *          <code>true</code> or <code>false</code>
   */
  public void setVisible(boolean show) {
    userInput.clear();
    editor.setVisible(show);
  }

  /**
   * Returns the user types values after dialog being displayed
   * 
   * @return a <code>Properties</code> object
   */
  public Properties getUserInput() {
    return userInput;
  }

  /**
   * Creates a <code>KeyListener</code> that enables a button whet input of
   * components is not null
   * 
   * @param ok
   *          The button to enable/disable
   * @param compos
   *          The list of components to test
   * @return a <code>KeyListener</code>
   */
  private static final KeyListener getAcceptButtonEnabler(final JButton ok,
      final JTextComponent... compos) {
    return new KeyAdapter() {

      public void keyPressed(java.awt.event.KeyEvent e) {
        boolean r = true;
        for (JTextComponent tc : compos)
          r &= ((tc.getText() != null) && (!("".equals(tc.getText()))));
        ok.setEnabled(r);
      }
    };
  }
}
