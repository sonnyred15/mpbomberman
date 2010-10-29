package org.amse.bomberman.server.net.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.amse.bomberman.common.net.netty.handlers.LoggingHandler;
import org.amse.bomberman.common.net.netty.handlers.ProtocolMessageDecoder;
import org.amse.bomberman.common.net.netty.handlers.ProtocolMessageEncoder;
import org.amse.bomberman.common.net.netty.handlers.ProtocolMessageFramer;

class ServerPipelineFactory implements ChannelPipelineFactory {

    private final ChannelGroup channels;

    public ServerPipelineFactory(ChannelGroup channels) {
        this.channels = channels;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        //log
        pipeline.addLast("log", new LoggingHandler());

        //framer
        FrameDecoder decoder = new ProtocolMessageFramer();
        pipeline.addLast("framer", decoder);

        //decoder
        pipeline.addLast("decoder", new ProtocolMessageDecoder());

        //encoder
        pipeline.addLast("encoder", new ProtocolMessageEncoder());

        //business logic
        pipeline.addLast("handler", new SessionHandler(channels));
        return pipeline;
    }
}
