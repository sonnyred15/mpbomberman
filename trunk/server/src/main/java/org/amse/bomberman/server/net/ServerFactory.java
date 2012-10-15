package org.amse.bomberman.server.net;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.amse.bomberman.common.threadfactory.DaemonThreadFactory;
import org.amse.bomberman.server.ServerConfig;
import org.amse.bomberman.server.ServiceContext;
import org.amse.bomberman.server.net.netty.NettyServer;
import org.amse.bomberman.server.net.tcpimpl.servers.TcpServer;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ServerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(ServerFactory.class);

    public ServerFactory() {}

    public Server newInstance(ServerConfig config, ServiceContext context) {
        String server = config.getServerType();
        if("netty".equalsIgnoreCase(server)) {
            return newNettyServer(context);
        } else if("simple".equalsIgnoreCase(server)) {
            return newSimpleServer(context);
        } else {
            LOG.error("Unknown server type(" + server + ")");
            throw new RuntimeException("Can`t instantiate server(" + server + ")");
        }
        
    }

    private Server newSimpleServer(ServiceContext context) {
        return new TcpServer(context);
    }

    private Server newNettyServer(ServiceContext context) {
        DaemonThreadFactory threadFactory = new DaemonThreadFactory();

        ExecutorService boss   = Executors.newCachedThreadPool(threadFactory);
        ExecutorService worker = Executors.newCachedThreadPool(threadFactory);

        NioServerSocketChannelFactory serverFactory
                = new NioServerSocketChannelFactory(boss, worker);

        Runtime.getRuntime().addShutdownHook(new ShutdownHookForNettyConnector(serverFactory));

        return new NettyServer(serverFactory, context);
    }

    private static class ShutdownHookForNettyConnector extends Thread {

        private final ChannelFactory clientFactory;

        private ShutdownHookForNettyConnector(ChannelFactory serverFactory) {
            this.clientFactory = serverFactory;
        }

        @Override
        public void run() {
            clientFactory.releaseExternalResources();
        }
    }
}