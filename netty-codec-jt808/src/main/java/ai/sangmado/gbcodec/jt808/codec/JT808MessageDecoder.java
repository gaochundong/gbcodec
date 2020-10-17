package ai.sangmado.gbcodec.jt808.codec;

import ai.sangmado.gbcodec.jt808.codec.serialization.JT808MessageNettyByteBufReader;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.IVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.JT808ProtocolVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.enums.JT808ProtocolVersion;
import ai.sangmado.gbprotocol.jt808.protocol.exceptions.UnsupportedJT808ProtocolVersionException;
import ai.sangmado.gbprotocol.jt808.protocol.message.IJT808VersioningMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteOrder;
import java.util.List;
import java.util.function.Supplier;

/**
 * JT808 协议解码器
 */
@Slf4j
@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class JT808MessageDecoder<T extends IJT808VersioningMessage> extends ByteToMessageDecoder {
    private final ISpecificationContext sctx;
    private final Supplier<T> messageSupplier;
    private final JT808MessageDecoderConfig config;

    public JT808MessageDecoder(ISpecificationContext sctx, Supplier<T> messageSupplier) {
        this(sctx, messageSupplier, new JT808MessageDecoderConfig());
    }

    public JT808MessageDecoder(ISpecificationContext sctx, Supplier<T> messageSupplier, JT808MessageDecoderConfig config) {
        this.sctx = sctx;
        this.messageSupplier = messageSupplier;
        this.config = config;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 检查Buffer长度
        final int minReadSize = 5;
        if (!in.isReadable(minReadSize)) {
            return;
        }

        // 反转义
        in.markReaderIndex();
        ByteBuf msg = readUnescapedBuf(in);
        in.resetReaderIndex();

        // 判断协议版本
        msg.markReaderIndex();
        JT808ProtocolVersion protocolVersion = determineProtocolVersion(msg);
        msg.resetReaderIndex();
        log.info("解码器接收到消息, 原始长度[{}], 转义后长度[{}], 识别协议版本[{}]",
                in.readableBytes(), msg.readableBytes(), protocolVersion.getName());
        msg.markReaderIndex();
        log.info("{}{}", System.lineSeparator(), ByteBufUtil.prettyHexDump(msg));
        msg.resetReaderIndex();

        // 使用新的协议版本上下文
        IVersionedSpecificationContext versionedContext =
                JT808ProtocolVersionedSpecificationContext.buildFrom(protocolVersion, sctx);

        // 解析消息包
        T message = messageSupplier.get();
        message.deserialize(versionedContext, new JT808MessageNettyByteBufReader(versionedContext, in));

        out.add(message);
    }

    /**
     * 判断协议版本
     */
    private JT808ProtocolVersion determineProtocolVersion(ByteBuf buf) {
        final int minReadSize = 5;
        if (!buf.isReadable(minReadSize)) {
            // 当前Buf长度不够读取到消息体属性和协议版本号，则放弃
            throw new UnsupportedJT808ProtocolVersionException(String.format(
                    "消息Buffer长度不足够, 长度[%s], 需要[%s]", buf.readableBytes(), minReadSize));
        }
        buf.readByte(); // 头标识
        int messageId = readWord(buf);
        int messageContentProperty = readWord(buf);
        int versionNumber = buf.readByte() & 0xFF;

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
            return JT808ProtocolVersion.V2011;
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
     * 将整体Buffer进行反转义
     */
    private ByteBuf readUnescapedBuf(ByteBuf in) {
        ByteBuf unescapedBuf = Unpooled.buffer(config.getDecodedBufferLength());
        while (in.isReadable()) {
            readUnescapedByte(in, unescapedBuf);
        }
        return unescapedBuf;
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
