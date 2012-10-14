package org.amse.bomberman.client.net.impl.netty;

import org.amse.bomberman.protocol.ProtocolMessage;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends SimpleChannelUpstreamHandler {//TODO not closing channel. Leak.

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final ClientHandlerListener listener;

    public ClientHandler(ClientHandlerListener listener) {
        this.listener = listener;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        if (!(e.getMessage() instanceof ProtocolMessage)) {
            throw new RuntimeException("Wrong type of message");
        }
        listener.received((ProtocolMessage) e.getMessage());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.error("Unexpected exception from downStream", e.getCause());
        e.getChannel().close().awaitUninterruptibly();
    }
}
