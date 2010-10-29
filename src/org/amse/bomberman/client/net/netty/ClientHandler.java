package org.amse.bomberman.client.net.netty;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.amse.bomberman.protocol.impl.ProtocolMessage;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

public class ClientHandler extends SimpleChannelUpstreamHandler {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());
    private final ClientHandlerListener listener;

    public ClientHandler(ClientHandlerListener listener) {
        this.listener = listener;
    }

    @Override//for what this?
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof ChannelStateEvent) {
            logger.info(e.toString());
        }
        super.handleUpstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (e instanceof ProtocolMessage) {
            listener.received((ProtocolMessage) e.getMessage());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.log(Level.SEVERE,
                "Unexpected exception from downStream",
                e.getCause());
        e.getChannel().close().awaitUninterruptibly();
    }
}
