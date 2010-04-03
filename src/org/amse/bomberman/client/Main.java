package org.amse.bomberman.client;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.view.mywizard.MainWizard;

/**
 *
 * @author Michail Korovkin
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainWizard wizard = new MainWizard();

        Controller.getInstance().setReceiveInfoListener(wizard);
    }
}
