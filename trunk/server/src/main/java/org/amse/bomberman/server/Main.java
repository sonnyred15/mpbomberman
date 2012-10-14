package org.amse.bomberman.server;

import org.amse.bomberman.server.view.ServerFrame;
import javax.swing.SwingUtilities;

/**
 * Main class of application.
 * 
 * @author Kirilchuk V.E.
 */
public class Main {

    /**
     * The entry-point of server part of application.
     * Initialize view.
     * 
     * @param args the command line arguments. Not supported.
     */
    public static void main(String[] args) {
        //TODO: make console start. GUI only for special
        //TODO: create config file in resources for server config
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ServerFrame();
            }
        });
    }
}
