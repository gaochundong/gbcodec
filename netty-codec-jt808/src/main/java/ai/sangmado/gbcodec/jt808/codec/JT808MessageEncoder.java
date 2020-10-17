package ai.sangmado.gbcodec.jt808.codec;

import ai.sangmado.gbcodec.jt808.codec.serialization.JT808MessageNettyByteBufWriter;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.IVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.IJT808VersioningMessage;
import ai.sangmado.gbprotocol.jt808.protocol.serialization.IJT808MessageBufferWriter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * JT808 协议编码器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808MessageEncoder<T extends IJT808VersioningMessage> extends MessageToMessageEncoder<Object> {
    private final ISpecificationContext sctx;
    private final JT808MessageEncoderConfig config;

    public JT808MessageEncoder(ISpecificationContext sctx) {
        this(sctx, new JT808MessageEncoderConfig());
    }

    public JT808MessageEncoder(ISpecificationContext sctx, JT808MessageEncoderConfig config) {
        this.sctx = sctx;
        this.config = config;
    }

    @Override
    @SuppressWarnings({"CastConflictsWithInstanceof"})
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (!(msg instanceof IJT808VersioningMessage)) {
            return;
        }

        IJT808VersioningMessage m = (IJT808VersioningMessage) msg;
        ByteBuf buf = ctx.alloc().buffer(config.getEncodedBufferLength());
        encodeMessage(buf, m);
        log.info("编码器接收到消息, 协议版本[{}], 消息ID[{}], 消息名称[{}], 编码后长度[{}]",
                m.getProtocolVersion().getName(),
                m.getMessageId().getName(),
                m.getMessageId().getDescription(),
                buf.readableBytes());

        buf.markReaderIndex();
        log.info("{}{}", System.lineSeparator(), ByteBufUtil.prettyHexDump(buf));
        buf.resetReaderIndex();

        out.add(buf);
    }

    private void encodeMessage(ByteBuf buf, IJT808VersioningMessage message) {
        // 使用新的协议版本上下文
        IVersionedSpecificationContext versionedContext =
                JT808ProtocolVersionedSpecificationContext.buildFrom(message.getProtocolVersion(), sctx);

        // 序列化消息
        IJT808MessageBufferWriter writer = new JT808MessageNettyByteBufWriter(versionedContext, buf);
        message.serialize(versionedContext, writer);
    }
}
