package org.amse.bomberman.common.net.netty.handlers;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.protocol.impl.ProtocolMessage;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ProtocolMessageDecoder extends OneToOneDecoder {

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof ChannelBuffer)) {
            return msg;
        }

        ChannelBuffer buffer = (ChannelBuffer) msg;

        ProtocolMessage message = new ProtocolMessage();
        message.setMessageId(buffer.readInt());

        List<String> data = fillData(buffer);
        message.setData(data);

        return message;
    }

    private List<String> fillData(ChannelBuffer buffer) {
        int size = buffer.readInt();
        List<String> result = new ArrayList<String>(size);

        for (int i = 0; i < size; ++i) {
            short strLen = buffer.readShort();
            ChannelBuffer buff = buffer.readSlice(strLen);
            result.add(buff.toString(Charset.forName("UTF-8")));
        }

        return result;
    }
}
