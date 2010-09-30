package org.amse.bomberman.client;

//~--- non-JDK imports --------------------------------------------------------

import javax.swing.SwingUtilities;
import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.net.impl.AsynchroConnector;
import org.amse.bomberman.client.view.bomberwizard.BomberWizard;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;

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
                new BomberWizard();
            }
        });
        //Shutdown hook to properly disconnect from server.
        Runtime.getRuntime().addShutdownHook(new ShutdowHook(AsynchroConnector.getInstance()));
    }

    private static class ShutdowHook extends Thread {
    private final Connector connector;

        public ShutdowHook(Connector connector) {
            this.connector = connector;
        }

        @Override
        public void run() {
            ProtocolMessage<Integer, String> exitMessage 
                    = new ProtocolMessage<Integer, String>();
            exitMessage.setMessageId(ProtocolConstants.DISCONNECT_MESSAGE_ID);
            try {
                connector.sendRequest(exitMessage);
            } catch (Exception ex) {
                //ignore
            } finally {
                connector.closeConnection();
            }
        }
    }
}
