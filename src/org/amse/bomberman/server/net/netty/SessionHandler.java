package org.amse.bomberman.server.net.netty;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.amse.bomberman.protocol.impl.ProtocolMessage;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;

/**
 *
 * @author Kirilchuk V.E.
 */
class SessionHandler extends SimpleChannelUpstreamHandler {
    private static final Logger logger = Logger.getLogger(SessionHandler.class.getName());

    private final ChannelGroup channels;

    public SessionHandler(ChannelGroup channels) {
        this.channels = channels;
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        channels.add(ctx.getChannel());
        super.channelConnected(ctx, e);
    }

    @Override//for what this?
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        logger.info(e.toString());
        super.handleUpstream(ctx, e);
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        ProtocolMessage message = (ProtocolMessage)e.getMessage();
        System.out.println("-----------------");
        System.out.println("MESSAGE RECEIVED ON SERVER:");
        System.out.println(message.getMessageId());
        for (String string : message.getData()) {
            System.out.println(string);
        }
        System.out.flush();
        System.out.println("-----------------");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        logger.log(Level.WARNING,
                "Unexpected exception from downStream",
                e.getCause());
        e.getChannel().close().awaitUninterruptibly();
        super.exceptionCaught(ctx, e);
    }
}
