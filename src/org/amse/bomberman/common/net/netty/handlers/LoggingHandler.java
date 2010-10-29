package org.amse.bomberman.common.net.netty.handlers;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;

/**
 *
 * @author Kirilchuk V.E.
 */
public class LoggingHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {

    private static final Logger logger = Logger.getLogger(LoggingHandler.class.getName());

    @Override
    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        log(e);
        ctx.sendUpstream(e);
    }

    @Override
    public void handleDownstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        log(e);
        ctx.sendDownstream(e);
    }

    private void log(ChannelEvent e) {
        if (logger.isLoggable(Level.FINEST)) {

            String msg = e.toString();
            // Log the message (and exception if available.)
            if (e instanceof ExceptionEvent) {
                logger.log(Level.WARNING, msg, ((ExceptionEvent) e).getCause());
            } else {
                logger.log(Level.FINEST, msg);
            }
        }
    }
}
