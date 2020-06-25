package ai.sangmado.gbcodec.jt808.codec;

import ai.sangmado.gbcodec.jt808.codec.serialization.JT808MessageNettyByteBufWriter;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.IJT808Message;
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
public class JT808MessageEncoder<T extends IJT808Message> extends MessageToMessageEncoder<Object> {
    private ISpecificationContext sctx;
    private JT808MessageEncoderConfig config;

    public JT808MessageEncoder(ISpecificationContext sctx) {
        this(sctx, new JT808MessageEncoderConfig());
    }

    public JT808MessageEncoder(ISpecificationContext sctx, JT808MessageEncoderConfig config) {
        this.sctx = sctx;
        this.config = config;
    }

    @Override
    @SuppressWarnings({"unchecked", "CastConflictsWithInstanceof"})
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (!(msg instanceof IJT808Message)) {
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

        buf.markReaderIndex();
        log.info("{}{}", System.lineSeparator(), ByteBufUtil.prettyHexDump(buf));
        buf.resetReaderIndex();

        out.add(buf);
    }

    private void encodeMessage(ByteBuf buf, T message) {
        // 使用新的协议上下文
        JT808ProtocolSpecificationContext newContext = buildNewContext(message);

        // 序列化消息
        IJT808MessageBufferWriter writer = new JT808MessageNettyByteBufWriter(newContext, buf);
        message.serialize(newContext, writer);
    }

    private JT808ProtocolSpecificationContext buildNewContext(T message) {
        JT808ProtocolSpecificationContext newContext = new JT808ProtocolSpecificationContext();
        newContext.setProtocolVersion(message.getProtocolVersion());
        newContext.setByteOrder(sctx.getByteOrder());
        newContext.setCharset(sctx.getCharset());
        newContext.setBufferPool(sctx.getBufferPool());
        return newContext;
    }
}
