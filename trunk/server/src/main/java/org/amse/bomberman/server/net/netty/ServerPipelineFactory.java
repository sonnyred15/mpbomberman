package org.amse.bomberman.server.net.netty;

import java.util.Set;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.amse.bomberman.common.net.netty.handlers.LoggingHandler;
import org.amse.bomberman.common.net.netty.handlers.ProtocolMessageDecoder;
import org.amse.bomberman.common.net.netty.handlers.ProtocolMessageEncoder;
import org.amse.bomberman.common.net.netty.handlers.ProtocolMessageFramer;
import org.amse.bomberman.server.net.Server;
import org.amse.bomberman.server.net.Session;

class ServerPipelineFactory implements ChannelPipelineFactory {

    private final Server server;
    private final Set<Session> sessions;

    public ServerPipelineFactory(Server server, Set<Session> sessions) {
        this.server = server;
        this.sessions = sessions;
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
        SessionHandler session = new SessionHandler(server, sessions);//TODO not good to give ref to server
        pipeline.addLast("handler", session);

        return pipeline;
    }
}
