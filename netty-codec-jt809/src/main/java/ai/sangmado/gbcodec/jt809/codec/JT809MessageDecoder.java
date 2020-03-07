package ai.sangmado.gbcodec.jt809.codec;

import ai.sangmado.gbcodec.jt809.codec.serialization.JT809MessageNettyByteBufReader;
import ai.sangmado.gbprotocol.jt809.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.JT809ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.enums.JT809ProtocolVersion;
import ai.sangmado.gbprotocol.jt809.protocol.message.JT809MessagePacket;
import ai.sangmado.gbprotocol.jt809.protocol.serialization.IJT809MessageBufferReader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * JT809 协议解码器
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT809MessageDecoder extends ByteToMessageDecoder {
    private ISpecificationContext sctx;

    public JT809MessageDecoder(ISpecificationContext sctx) {
        this.sctx = sctx;
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
        IJT809MessageBufferReader reader = new JT809MessageNettyByteBufReader(newContext, in);
        JT809MessagePacket packet = new JT809MessagePacket();
        packet.deserialize(newContext, reader);

        out.add(packet);
    }
}