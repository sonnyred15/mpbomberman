package org.amse.bomberman.server.net.netty;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import org.amse.bomberman.server.ServiceContext;
import org.amse.bomberman.server.gameservice.GameStorage;
import org.amse.bomberman.server.net.Server;
import org.amse.bomberman.server.net.Session;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ServerChannelFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NettyServer implements Server {
    private static final Logger logger
            = Logger.getLogger(NettyServer.class.getName());

    private final ServerChannelFactory factory;
    private final ChannelGroup channels = new DefaultChannelGroup("Server");
    private Channel server;

    public NettyServer(ServerChannelFactory factory) {
        this.factory = factory;
    }

    @Override
    public void start(int port) {
        if (server != null && server.isBound()) {
            throw new IllegalStateException("Already in started state.");
        }

        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService worker = Executors.newCachedThreadPool();

        NioServerSocketChannelFactory factory
                = new NioServerSocketChannelFactory(boss, worker);

        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        //configuring server
        bootstrap.setPipelineFactory(new ServerPipelineFactory(channels));

        //binding to port
        server = bootstrap.bind(new InetSocketAddress(port));
        
        logger.info("Server started");
    }

    @Override
    public void shutdown() {
        if (server == null || !server.isBound()) {
            throw new IllegalStateException("Already in shutdowned state.");
        }

        server.close().awaitUninterruptibly();
        logger.info("Server end accepting clients.");
    }

    public boolean isShutdowned() {
        if (server == null || !server.isBound()) {
            return true;
        }

        return false;
    }

    @Override
    public int getPort() {
        if(isShutdowned()) {
            throw new IllegalStateException("Can`t get port of shutdowned server.");
        }
        return ((InetSocketAddress)server.getLocalAddress()).getPort();
    }

    @Override
    public boolean isStopped() {
        return server.isBound();
    }

    @Override
    public ServiceContext getServiceContext() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public Set<Session> getSessions() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void sessionTerminated(Session endedSession) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
