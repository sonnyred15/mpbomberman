package org.amse.bomberman.client.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.amse.bomberman.client.net.impl.netty.NettyConnector;
import org.amse.bomberman.client.net.stdtcp.impl.ConnectorImpl;
import org.amse.bomberman.common.threadfactory.DaemonThreadFactory;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ConnectorFactory {
    private static final Logger logger = LoggerFactory.getLogger(ConnectorFactory.class);

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
                logger.warn("No connector config founded.");
            } else {
                config.load(in);
            }
        } catch (IOException ex) {
            logger.error("IOException while reading connector config.", ex);
        }
    }

    public GenericConnector<ProtocolMessage> newInstance() {
        String connector = config.getProperty("connector");
        if("netty".equalsIgnoreCase(connector)) {
            return newNettyConnector();
        } else if("default".equalsIgnoreCase(connector)) {
            return newSimpleConnector();
        } else {
            logger.error("No factory for specified connector(" + connector + ")");
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
