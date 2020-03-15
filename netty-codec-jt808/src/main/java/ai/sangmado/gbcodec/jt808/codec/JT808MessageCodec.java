package ai.sangmado.gbcodec.jt808.codec;

import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * JT808 协议编解码器
 */
@SuppressWarnings("FieldCanBeLocal")
public final class JT808MessageCodec extends CombinedChannelDuplexHandler<JT808MessageDecoder, JT808MessageEncoder<JT808MessagePacket>> {
    private ISpecificationContext sctx;

    public JT808MessageCodec(ISpecificationContext sctx) {
        this(sctx, new JT808MessageDecoder(sctx), new JT808MessageEncoder<>(sctx));
    }

    public JT808MessageCodec(ISpecificationContext sctx, JT808MessageDecoder decoder, JT808MessageEncoder<JT808MessagePacket> encoder) {
        this.sctx = sctx;
        init(decoder, encoder);
    }
}
