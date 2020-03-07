package ai.sangmado.gbcodec.jt809.codec;

import ai.sangmado.gbcodec.jt809.codec.serialization.JT809MessageNettyByteBufWriter;
import ai.sangmado.gbprotocol.jt809.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.JT809ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.message.IJT809Message;
import ai.sangmado.gbprotocol.jt809.protocol.serialization.IJT809MessageBufferWriter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * JT809 协议编码器
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT809MessageEncoder<T extends IJT809Message> extends MessageToMessageEncoder<Object> {
    private ISpecificationContext sctx;

    public JT809MessageEncoder(ISpecificationContext sctx) {
        this.sctx = sctx;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        ByteBuf buf = null;
        if (msg instanceof IJT809Message) {
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
        JT809ProtocolSpecificationContext newContext = new JT809ProtocolSpecificationContext();
        newContext.setProtocolVersion(message.getProtocolVersion());
        newContext.setByteOrder(sctx.getByteOrder());
        newContext.setCharset(sctx.getCharset());
        newContext.setMessageContentEncryptionOptions(sctx.getMessageContentEncryptionOptions());
        newContext.setBufferPool(sctx.getBufferPool());

        // 序列化消息
        IJT809MessageBufferWriter writer = new JT809MessageNettyByteBufWriter(newContext, buf);
        message.serialize(newContext, writer);
    }
}
