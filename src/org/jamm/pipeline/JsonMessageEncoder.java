package org.jamm.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.jamm.message.JsonMessage;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

public class JsonMessageEncoder extends MessageToMessageEncoder<JsonMessage> {

    // TODO Use CharsetEncoder instead.
    private final Charset charset;

    public JsonMessageEncoder() {
        this(Charset.defaultCharset());
    }

    public JsonMessageEncoder(Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, JsonMessage msg, List<Object> out) throws Exception {
        if (msg.getBody().size() == 0) {
            return;
        }
        out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg.buildPacketMessage()), charset));
    }
}
