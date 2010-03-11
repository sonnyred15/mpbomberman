/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server;

import javax.swing.SwingUtilities;

/**
 *
 * @author chibis
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new ServerFrame();
            }
        });
    }
}
