package org.amse.bomberman.server;

import java.io.IOException;

import org.amse.bomberman.server.net.Server;
import org.amse.bomberman.server.net.ServerFactory;
import org.amse.bomberman.server.util.ServerConfigLoader;

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
     * @throws IOException if io errors occur during configuration load
     */
    public static void main(String[] args) throws IOException {
        ServerConfigLoader configLoader = new ServerConfigLoader();
        ServerConfig config = configLoader.load();
        ServerFactory serverFactory = new ServerFactory();
        ServiceContext context = new ServiceContext();
        Server server = serverFactory.newInstance(config, context);
        server.start(config);
        
//        What about some stats printed to console?
//        private final String CLIENTS_LABEL_TEXT         = "Clients: ";
//        private final String PORT_LABEL_TEXT            = "Port: ";
//        private final String SHUTDOWNED_LABEL_TEXT      = "Shutdowned: ";
//        private final String STARTED_GAMES_LABEL_TEXT   = "Started games: ";
//        private final String UNSTARTED_GAMES_LABEL_TEXT = "Unstarted games: ";
    }
}
