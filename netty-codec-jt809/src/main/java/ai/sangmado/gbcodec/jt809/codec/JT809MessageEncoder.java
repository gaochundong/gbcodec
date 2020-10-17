package ai.sangmado.gbcodec.jt809.codec;

import ai.sangmado.gbcodec.jt809.codec.serialization.JT809MessageNettyByteBufWriter;
import ai.sangmado.gbprotocol.jt809.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.IVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.JT809ProtocolVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.message.IJT809VersioningMessage;
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
public class JT809MessageEncoder<T extends IJT809VersioningMessage> extends MessageToMessageEncoder<Object> {
    private final ISpecificationContext sctx;
    private final JT809MessageEncoderConfig config;

    public JT809MessageEncoder(ISpecificationContext sctx) {
        this(sctx, new JT809MessageEncoderConfig());
    }

    public JT809MessageEncoder(ISpecificationContext sctx, JT809MessageEncoderConfig config) {
        this.sctx = sctx;
        this.config = config;
    }

    @Override
    @SuppressWarnings({"CastConflictsWithInstanceof"})
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (!(msg instanceof IJT809VersioningMessage)) {
            return;
        }

        IJT809VersioningMessage m = (IJT809VersioningMessage) msg;
        ByteBuf buf = ctx.alloc().buffer(config.getEncodedBufferLength());
        encodeMessage(buf, m);
        log.info("编码器接收到消息, 协议版本[{}], 消息ID[{}], 消息名称[{}], 编码后长度[{}]",
                m.getProtocolVersion().getName(),
                m.getMessageId().getName(),
                m.getMessageId().getDescription(),
                buf.readableBytes());

        out.add(buf);
    }

    private void encodeMessage(ByteBuf buf, IJT809VersioningMessage message) {
        // 使用新的协议版本上下文
        IVersionedSpecificationContext versionedContext =
                JT809ProtocolVersionedSpecificationContext.buildFrom(message.getProtocolVersion(), sctx);

        // 序列化消息
        IJT809MessageBufferWriter writer = new JT809MessageNettyByteBufWriter(versionedContext, buf);
        message.serialize(versionedContext, writer);
    }
}
