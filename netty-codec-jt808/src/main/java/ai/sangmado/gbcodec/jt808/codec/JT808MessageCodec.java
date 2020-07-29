package ai.sangmado.gbcodec.jt808.codec;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.IJT808VersioningMessage;
import io.netty.channel.CombinedChannelDuplexHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * JT808 协议编解码器
 *
 * @param <T> JT808 消息类
 */
@Slf4j
@SuppressWarnings("FieldCanBeLocal")
public final class JT808MessageCodec<T extends IJT808VersioningMessage>
        extends CombinedChannelDuplexHandler<JT808MessageDecoder<T>, JT808MessageEncoder<T>> {
    private final ISpecificationContext sctx;

    public JT808MessageCodec(ISpecificationContext sctx, Supplier<T> messageSupplier) {
        this(sctx, new JT808MessageDecoder<>(sctx, messageSupplier), new JT808MessageEncoder<>(sctx));
    }

    private JT808MessageCodec(ISpecificationContext sctx, JT808MessageDecoder<T> decoder, JT808MessageEncoder<T> encoder) {
        this.sctx = sctx;
        init(decoder, encoder);
    }
}
