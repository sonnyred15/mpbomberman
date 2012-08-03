package org.amse.bomberman.client.net.impl.netty;

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;
import org.amse.bomberman.client.net.ConnectorListener;
import org.amse.bomberman.client.net.GenericConnector;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.protocol.GenericProtocolMessage;
import org.amse.bomberman.protocol.impl.ProtocolMessage;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.ClientSocketChannelFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NettyConnector implements GenericConnector<ProtocolMessage>, ClientHandlerListener {
    private static Logger logger = Logger.getLogger(NettyConnector.class.getName());

    private final ClientSocketChannelFactory factory;

    private ClientBootstrap bootstrap;
    private Channel connection;
    private ConnectorListener listener;

    public NettyConnector(ClientSocketChannelFactory factory) {
        this.factory = factory;
    }

    /**
     *
     * @param host
     * @param port
     * @throws UnknownHostException
     * @throws IOException
     */
    @Override
    public void сonnect(InetAddress host, int port) throws ConnectException {
        if(connection != null && connection.isConnected()) {
            throw new IllegalStateException("Already connected. Disconnect first.");
        }

        initBootstrap(factory);

        // Trying to connect
        ChannelFuture connect = bootstrap.connect(new InetSocketAddress(host, port));
        connect.awaitUninterruptibly();

        if(!connect.isSuccess()) {
            logger.warning("Client failed to connect to " + host + ":" + port);            
            throw new ConnectException(connect.getCause().getMessage());
        } else {
            connection = connect.getChannel();            
        }
        logger.info("Connection established with ." + host + ":" + port);
    }

    @Override
    public void send(ProtocolMessage message) {
        if(connection == null || !connection.isConnected()) {
            throw new IllegalStateException("You are not connected");
        }
        connection.write(message);
        logger.info("Sended message to channel asynchronously.");
    }

    @Override
    public void closeConnection() {
        if(connection == null) {
            return;
        }
        
        if(connection.isOpen()) {
            connection.close().awaitUninterruptibly();
        }
        logger.info("Connection closed.");
    }
    
    private void initBootstrap(ClientSocketChannelFactory chanelFactory) {
        //creating bootstrap
        bootstrap = new ClientBootstrap();
        bootstrap.setFactory(chanelFactory);
        bootstrap.setPipelineFactory(new ClientPipelineFactory(this));
    }

    @Override
    public void setListener(ConnectorListener listener) {
        this.listener = listener;
    }

    @Override
    public void received(ProtocolMessage message) {
        if(listener != null) {
            listener.received(message);
        }
    }
}