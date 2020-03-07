package ai.sangmado.gbcodec.jt809.codec;

import ai.sangmado.gbprotocol.jt809.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.message.JT809MessagePacket;
import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * JT809 协议编解码器
 */
@SuppressWarnings("FieldCanBeLocal")
public final class JT809MessageCodec
        extends CombinedChannelDuplexHandler<JT809MessageDecoder, JT809MessageEncoder<JT809MessagePacket>> {
    private ISpecificationContext sctx;

    public JT809MessageCodec(ISpecificationContext sctx) {
        this.sctx = sctx;
        init(new JT809MessageDecoder(sctx), new JT809MessageEncoder<>(sctx));
    }
}
