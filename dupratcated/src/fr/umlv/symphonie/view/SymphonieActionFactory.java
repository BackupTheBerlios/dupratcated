/*
 * This file is part of Symphonie Created : 17 févr. 2005 22:35:15
 */
package fr.umlv.symphonie.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import fr.umlv.symphonie.util.SymphonieComponentBuilder;


public class SymphonieActionFactory {

  /* FILE ACTIONS ******************************************/
  public static AbstractAction getExitAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        System.exit(0);
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getPrintAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getProxyAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getImportAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getExportAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  /* WINDOW ACTIONS ******************************************/
  public static AbstractAction getModeAction(Icon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getModeChangeAction(String view, ImageIcon icon) {
    AbstractAction a = new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getLanguageAction(Icon icon) {
    AbstractAction a = new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getLanguageChangeAction(
      final HashMap<String, String> resources,
      final SymphonieComponentBuilder builder) {
    return new AbstractAction() {

      public void actionPerformed(ActionEvent event) {
        builder.setNameMap(resources);
      }
    };
  }
  
  public static AbstractAction getFormulaAction(Icon icon) {
    AbstractAction a = new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  public static AbstractAction getFormulaCellAction(Icon icon) {
    AbstractAction a = new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getColumnAction(Icon icon) {
    AbstractAction a = new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getLineAction(Icon icon) {
    AbstractAction a = new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getConnectAction(Icon icon) {
    AbstractAction a = new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getDBAction(Icon icon) {
    AbstractAction a = new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }
  
  public static AbstractAction getPwdAction(Icon icon) {
    AbstractAction a = new AbstractAction() {
      public void actionPerformed(ActionEvent event) {
      }
    };
    a.putValue(Action.SMALL_ICON, icon);
    return a;
  }

}
