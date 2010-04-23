package org.amse.bomberman.client;

//~--- non-JDK imports --------------------------------------------------------

import javax.swing.SwingUtilities;
import org.amse.bomberman.client.view.mywizard.BombWizard;

/**
 *
 * @author Michail Korovkin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                //new BomberWizard();
                new BombWizard();
            }
        });
    }
}
