package org.amse.bomberman.common.net.netty.handlers;

import org.jboss.netty.channel.ChannelDownstreamHandler;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.ExceptionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
public class LoggingHandler implements ChannelUpstreamHandler, ChannelDownstreamHandler {

    private static final Logger logger = LoggerFactory.getLogger(LoggingHandler.class);

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
        if (logger.isTraceEnabled()) {

            String msg = e.toString();
            // Log the message (and exception if available.)
            if (e instanceof ExceptionEvent) {
                logger.trace(msg, ((ExceptionEvent) e).getCause());
            } else {
                logger.trace(msg);
            }
        }
    }
}
