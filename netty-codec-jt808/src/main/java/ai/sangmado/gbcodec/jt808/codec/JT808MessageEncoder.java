package ai.sangmado.gbcodec.jt808.codec;

import ai.sangmado.gbcodec.jt808.codec.serialization.JT808MessageNettyByteBufWriter;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.IJT808Message;
import ai.sangmado.gbprotocol.jt808.protocol.serialization.IJT808MessageBufferWriter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * JT808 协议编码器
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT808MessageEncoder<T extends IJT808Message> extends MessageToMessageEncoder<Object> {
    private ISpecificationContext sctx;

    public JT808MessageEncoder(ISpecificationContext sctx) {
        this.sctx = sctx;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        ByteBuf buf = null;
        if (msg instanceof IJT808Message) {
            @SuppressWarnings({"unchecked", "CastConflictsWithInstanceof"})
            T m = (T) msg;
            buf = ctx.alloc().buffer(256);
            encodeMessage(buf, m);
        }
        if (buf != null) {
            out.add(buf);
        }
    }

    private void encodeMessage(ByteBuf buf, T message) {
        // 使用新的协议上下文
        JT808ProtocolSpecificationContext newContext = new JT808ProtocolSpecificationContext();
        newContext.setProtocolVersion(message.getProtocolVersion());
        newContext.setByteOrder(sctx.getByteOrder());
        newContext.setCharset(sctx.getCharset());
        newContext.setBufferPool(sctx.getBufferPool());

        // 序列化消息
        IJT808MessageBufferWriter writer = new JT808MessageNettyByteBufWriter(newContext, buf);
        message.serialize(newContext, writer);
    }
}
