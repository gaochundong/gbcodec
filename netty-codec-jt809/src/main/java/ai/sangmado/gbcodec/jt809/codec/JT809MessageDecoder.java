package ai.sangmado.gbcodec.jt809.codec;

import ai.sangmado.gbcodec.jt809.codec.serialization.JT809MessageNettyByteBufReader;
import ai.sangmado.gbprotocol.jt809.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.JT809ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.enums.JT809ProtocolVersion;
import ai.sangmado.gbprotocol.jt809.protocol.message.IJT809VersioningMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Supplier;

/**
 * JT809 协议解码器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT809MessageDecoder<T extends IJT809VersioningMessage> extends ByteToMessageDecoder {
    private final ISpecificationContext sctx;
    private final Supplier<T> messageSupplier;
    private final JT809MessageDecoderConfig config;

    public JT809MessageDecoder(ISpecificationContext sctx, Supplier<T> messageSupplier) {
        this(sctx, messageSupplier, new JT809MessageDecoderConfig());
    }

    public JT809MessageDecoder(ISpecificationContext sctx, Supplier<T> messageSupplier, JT809MessageDecoderConfig config) {
        this.sctx = sctx;
        this.messageSupplier = messageSupplier;
        this.config = config;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        JT809ProtocolVersion protocolVersion = JT809ProtocolVersion.V2011;
        //if (protocolVersion == null) return;

        // 使用新的协议上下文
        JT809ProtocolSpecificationContext newContext = new JT809ProtocolSpecificationContext();
        newContext.setProtocolVersion(protocolVersion);
        newContext.setByteOrder(sctx.getByteOrder());
        newContext.setCharset(sctx.getCharset());
        newContext.setMessageContentEncryptionOptions(sctx.getMessageContentEncryptionOptions());
        newContext.setBufferPool(sctx.getBufferPool());

        // 解析消息包
        T message = messageSupplier.get();
        message.deserialize(newContext, new JT809MessageNettyByteBufReader(newContext, in));

        out.add(message);
    }
}
