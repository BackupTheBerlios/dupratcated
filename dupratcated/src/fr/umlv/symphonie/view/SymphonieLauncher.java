/*
 * This file is part of Symphonie
 * Created : 19 mars 2005 21:11:30
 */

package fr.umlv.symphonie.view;

import java.io.IOException;

import javax.swing.JFrame;

import fr.umlv.symphonie.data.DataManagerException;

/**
 * Symphonie application entry point.
 */
public abstract class SymphonieLauncher {

  public static void main(String[] args) throws DataManagerException,
      IOException {

    System.setProperty("sun.awt.exception.handler", EDTExceptionHandler.class
        .getName());
    Symphonie s = new Symphonie();
    EDTExceptionHandler.setDisplayer(s.errDisplay);
    JFrame f = s.getFrame();
    f.pack();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setVisible(true);
  }
}
