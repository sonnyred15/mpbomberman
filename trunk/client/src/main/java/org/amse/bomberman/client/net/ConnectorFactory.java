package org.amse.bomberman.client.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.amse.bomberman.client.net.impl.netty.NettyConnector;
import org.amse.bomberman.client.net.stdtcp.impl.ConnectorImpl;
import org.amse.bomberman.common.threadfactory.DaemonThreadFactory;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ConnectorFactory {
    private static final Logger logger = Logger.getLogger(ConnectorFactory.class.getName());

    private final Properties defaults = new Properties();
    {
        defaults.setProperty("connector", "default");
    }

    private final Properties config;

    public ConnectorFactory() {
        config = new Properties(defaults);
        try {
            InputStream in = getClass().getResourceAsStream("/connector.conf");
            if(in == null) {
                logger.log(Level.SEVERE, "No connector config founded.");
            } else {
                config.load(in);
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "IOException while reading connector config.", ex);
        }
    }

    public GenericConnector<ProtocolMessage> newInstance() {
        String connector = config.getProperty("connector");
        if("netty".equalsIgnoreCase(connector)) {
            return newNettyConnector();
        } else if("default".equalsIgnoreCase(connector)) {
            return newSimpleConnector();
        } else {
            logger.log(Level.SEVERE, "No factory for specified connector(" + connector + ")");
            throw new RuntimeException("Can`t instantiate connector(" + connector + ")");
        }
    }

    private GenericConnector<ProtocolMessage> newNettyConnector() {
        DaemonThreadFactory threadFactory = new DaemonThreadFactory();
        
        ExecutorService boss   = Executors.newCachedThreadPool(threadFactory);
        ExecutorService worker = Executors.newCachedThreadPool(threadFactory);

        NioClientSocketChannelFactory clientFactory
                = new NioClientSocketChannelFactory(boss, worker);

        Runtime.getRuntime().addShutdownHook(new ShutdownHookForNettyConnector(clientFactory));

        return new NettyConnector(clientFactory);
    }

    private GenericConnector<ProtocolMessage> newSimpleConnector() {
        return new ConnectorImpl();
    }

    private static class ShutdownHookForNettyConnector extends Thread {

        private final ChannelFactory clientFactory;

        private ShutdownHookForNettyConnector(ChannelFactory clientFactory) {
            this.clientFactory = clientFactory;
        }

        @Override
        public void run() {
            clientFactory.releaseExternalResources();
        }
    }
}
