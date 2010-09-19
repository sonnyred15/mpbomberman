/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server;

import org.amse.bomberman.server.view.ServerFrame;
import javax.swing.SwingUtilities;

/**
 * Main class of application.
 * @author Kirilchuk V.E.
 */
public class Main {

    /**
     * The entry-point of server part of application.
     * Initialize view.
     * @param args the command line arguments. Not supported.
     */
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ServerFrame();
            }
        });
    }
}
