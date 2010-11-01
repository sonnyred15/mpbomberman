package org.amse.bomberman.client.net.impl.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.amse.bomberman.common.net.netty.handlers.LoggingHandler;
import org.amse.bomberman.common.net.netty.handlers.ProtocolMessageDecoder;
import org.amse.bomberman.common.net.netty.handlers.ProtocolMessageEncoder;
import org.amse.bomberman.common.net.netty.handlers.ProtocolMessageFramer;
import org.jboss.netty.channel.ChannelHandler;

class ClientPipelineFactory implements ChannelPipelineFactory {
    // Stateless handlers.
    private static final ChannelHandler LOG_HANDLER = new LoggingHandler();
    private static final ChannelHandler FRAMER = new ProtocolMessageFramer();
    private static final ChannelHandler DECODER = new ProtocolMessageDecoder();
    private static final ChannelHandler ENCODER = new ProtocolMessageEncoder();

    private final ClientHandlerListener listener;

    public ClientPipelineFactory(ClientHandlerListener listener) {
        this.listener = listener;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception {
        ChannelPipeline pipeline = Channels.pipeline();

        //log
        pipeline.addLast("log", LOG_HANDLER);

        //framer
        pipeline.addLast("framer", FRAMER);

        //decoder
        pipeline.addLast("decoder", DECODER);

        //encoder
        pipeline.addLast("encoder", ENCODER);

        //business logic
        pipeline.addLast("handler", new ClientHandler(listener));//ClientHandler is stateful, so always new.
        return pipeline;
    }
}
