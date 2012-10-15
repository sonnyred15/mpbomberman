package org.amse.bomberman.server.net.netty;

import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.amse.bomberman.server.ServerConfig;
import org.amse.bomberman.server.ServiceContext;
import org.amse.bomberman.server.net.Server;
import org.amse.bomberman.server.net.Session;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ServerChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NettyServer implements Server {
    private static final Logger LOG = LoggerFactory.getLogger(NettyServer.class);

    private Channel server;

    private final ServerChannelFactory factory;
    private final ServiceContext       context;
    private final Set<Session>         sessions = new CopyOnWriteArraySet<Session>();

    public NettyServer(ServerChannelFactory factory, ServiceContext context) {
        this.factory = factory;
        this.context = context;
    }

    @Override
    public synchronized void start(ServerConfig config) {
        if (server != null && server.isBound()) {
            throw new IllegalStateException("Already in started state.");
        }

        ServerBootstrap bootstrap = new ServerBootstrap(factory);

        //configuring server
        bootstrap.setPipelineFactory(new ServerPipelineFactory(this, sessions));

        //binding to port
        server = bootstrap.bind(new InetSocketAddress(config.getPort()));        
        
        LOG.info("Server started");
    }

    @Override
    public synchronized void shutdown() {
        if (server == null || !server.isBound()) {
            throw new IllegalStateException("Already in shutdowned state.");
        }

        server.close().awaitUninterruptibly();
        for (Session session : sessions) {
            session.terminateSession();
        }
        context.getGameStorage().clearGames();
        LOG.info("Server end accepting clients.");
    }

    @Override
    public synchronized boolean isShutdowned() {
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
    public ServiceContext getServiceContext() {
        return context;
    }

    @Override
    public Set<Session> getSessions() {
        return sessions;
    }

    @Override
    public void sessionTerminated(Session endedSession) {
        sessions.remove(endedSession);
    }
}
