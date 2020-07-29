package ai.sangmado.gbcodec.jt809.codec;

import ai.sangmado.gbprotocol.jt809.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.message.IJT809VersioningMessage;
import io.netty.channel.CombinedChannelDuplexHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * JT809 协议编解码器
 *
 * @param <T> JT809 消息类
 */
@Slf4j
@SuppressWarnings("FieldCanBeLocal")
public final class JT809MessageCodec<T extends IJT809VersioningMessage>
        extends CombinedChannelDuplexHandler<JT809MessageDecoder<T>, JT809MessageEncoder<T>> {
    private final ISpecificationContext sctx;

    public JT809MessageCodec(ISpecificationContext sctx, Supplier<T> messageSupplier) {
        this(sctx, new JT809MessageDecoder<>(sctx, messageSupplier), new JT809MessageEncoder<>(sctx));
    }

    private JT809MessageCodec(ISpecificationContext sctx, JT809MessageDecoder<T> decoder, JT809MessageEncoder<T> encoder) {
        this.sctx = sctx;
        init(decoder, encoder);
    }
}
