package ai.sangmado.gbcodec.jt809.codec;

import ai.sangmado.gbcodec.jt809.codec.serialization.JT809MessageNettyByteBufWriter;
import ai.sangmado.gbprotocol.jt809.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.JT809ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.message.IJT809Message;
import ai.sangmado.gbprotocol.jt809.protocol.serialization.IJT809MessageBufferWriter;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JT809 协议编码器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT809MessageEncoder<T extends IJT809Message> extends MessageToMessageEncoder<Object> {
    private ISpecificationContext sctx;
    private JT809MessageEncoderConfig config;

    public JT809MessageEncoder(ISpecificationContext sctx) {
        this(sctx, new JT809MessageEncoderConfig());
    }

    public JT809MessageEncoder(ISpecificationContext sctx, JT809MessageEncoderConfig config) {
        this.sctx = sctx;
        this.config = config;
    }

    @Override
    @SuppressWarnings({"unchecked", "CastConflictsWithInstanceof"})
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (!(msg instanceof IJT809Message)) {
            return;
        }

        T m = (T) msg;
        ByteBuf buf = ctx.alloc().buffer(config.getEncodedBufferLength());
        encodeMessage(buf, m);
        log.info("编码器接收到消息, 协议版本[{}], 消息ID[{}], 消息名称[{}], 编码后长度[{}]",
                m.getProtocolVersion().getName(),
                m.getMessageId().getName(),
                m.getMessageId().getDescription(),
                buf.readableBytes());

        out.add(buf);
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
