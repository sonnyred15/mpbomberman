/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.common.net.netty.handlers;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ProtocolMessageFramer extends FrameDecoder {

    private static final int BYTES_IN_MSG_ID = 4;
    private static final int BYTES_IN_SIZE = 4;
    private static final int BYTES_IN_HEADER = BYTES_IN_MSG_ID + BYTES_IN_SIZE;
    private static final int BYTES_IN_STR_LEN = 2;

    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer) throws Exception {
        
        if (buffer.readableBytes() < BYTES_IN_HEADER) {
            return null;
        }

        int shift = buffer.readerIndex() + BYTES_IN_MSG_ID;

        int size = buffer.getInt(shift);

        ChannelBuffer frame = null;

        if (size == 0) {//return only header. No data.
            frame = buffer.readBytes(BYTES_IN_HEADER);
            return frame;
        }

        shift += BYTES_IN_SIZE;//now points to first string length
        for (int i = 0; i < size; ++i) {            
            short strLen = buffer.getShort(shift);
            shift += (BYTES_IN_STR_LEN + strLen);
            if (buffer.readableBytes() < amount(shift, buffer)) {
                return null;
            }
        }
        
        frame = buffer.readBytes(amount(shift, buffer));

        return frame;
    }

    private int amount(int shift, ChannelBuffer buffer) {
        return shift - buffer.readerIndex();
    }
}
