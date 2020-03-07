package ai.sangmado.gbcodec.jt808.codec;

import ai.sangmado.gbcodec.jt808.codec.serialization.JT808MessageNettyByteBufReader;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.exceptions.UnsupportedJT808ProtocolVersionException;
import ai.sangmado.gbprotocol.jt808.protocol.message.JT808MessagePacket;
import ai.sangmado.gbprotocol.jt808.protocol.serialization.IJT808MessageBufferReader;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.nio.ByteOrder;
import java.util.List;

/**
 * JT808 协议解码器
 */
@SuppressWarnings("FieldCanBeLocal")
public class JT808MessageDecoder extends ByteToMessageDecoder {
    private ISpecificationContext sctx;

    public JT808MessageDecoder(ISpecificationContext sctx) {
        this.sctx = sctx;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        JT808ProtocolVersion protocolVersion = determineProtocolVersion(in);
        if (protocolVersion == null) return;

        // 使用新的协议上下文
        JT808ProtocolSpecificationContext newContext = new JT808ProtocolSpecificationContext();
        newContext.setProtocolVersion(protocolVersion);
        newContext.setByteOrder(sctx.getByteOrder());
        newContext.setCharset(sctx.getCharset());
        newContext.setBufferPool(sctx.getBufferPool());

        // 解析消息包
        IJT808MessageBufferReader reader = new JT808MessageNettyByteBufReader(newContext, in);
        JT808MessagePacket packet = new JT808MessagePacket();
        packet.deserialize(newContext, reader);

        out.add(packet);
    }

    /**
     * 判断协议版本
     */
    private JT808ProtocolVersion determineProtocolVersion(ByteBuf in) {
        // 读取消息头部(头标识+消息ID+消息体属性+协议版本号)，判断协议版本
        in.markReaderIndex();
        in.readByte();
        int readNeeded = 9, readCount = 0;
        ByteBuf tmpHeaderBuf = Unpooled.buffer(readCount);
        while (in.isReadable() && (readCount < readNeeded)) {
            readUnescapedByte(in, tmpHeaderBuf);
            readCount++;
        }
        in.resetReaderIndex();
        if (!tmpHeaderBuf.isReadable(5)) {
            // 当前Buf长度不够读取到消息体属性和协议版本号，则放弃
            return null;
        }
        int messageId = readWord(tmpHeaderBuf);
        int messageContentProperty = readWord(tmpHeaderBuf);
        int versionNumber = tmpHeaderBuf.readByte() & 0xFF;

        // 通过消息体属性格式中第14位版本位尝试判断协议版本
        if ((messageContentProperty >> 14 & 0x01) == 1) {
            // 2019版本，此标记位为1.
            if (versionNumber == 1) {
                return JT808ProtocolVersion.V2019;
            } else {
                throw new UnsupportedJT808ProtocolVersionException(String.format(
                        "协议版本不支持，消息ID[%s]，消息体属性[%s]，协议版本号[%s]",
                        messageId, messageContentProperty, versionNumber));
            }
        } else {
            // 2013版本与2011版本相同，此标记位为0.
            return JT808ProtocolVersion.V2013;
        }
    }

    /**
     * 读取 WORD 无符号双字节整型 (字节，16位)
     */
    @SuppressWarnings("IfStatementWithIdenticalBranches")
    private int readWord(ByteBuf buf) {
        if (sctx.getByteOrder() == ByteOrder.BIG_ENDIAN) {
            return (((buf.readByte() & 0xFF) << 8) | ((buf.readByte() & 0xFF))) & 0xFFFF;
        } else {
            return (((buf.readByte() & 0xFF)) | ((buf.readByte() & 0xFF) << 8)) & 0xFFFF;
        }
    }

    /**
     * 反向标识位转义
     */
    private void readUnescapedByte(ByteBuf reader, ByteBuf writer) {
        byte b1 = reader.readByte();
        if (b1 == (byte) 0x7d) {
            if (reader.isReadable()) {
                byte b2 = reader.readByte();
                if (b2 == (byte) 0x01) {
                    writer.writeByte(0x7d);
                } else if (b2 == (byte) 0x02) {
                    writer.writeByte(0x7e);
                } else {
                    writer.writeByte(b1);
                    writer.writeByte(b2);
                }
            }
        } else {
            writer.writeByte(b1);
        }
    }
}
