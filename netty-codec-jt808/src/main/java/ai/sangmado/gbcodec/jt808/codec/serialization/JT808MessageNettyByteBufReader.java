package ai.sangmado.gbcodec.jt808.codec.serialization;

import ai.sangmado.gbprotocol.gbcommon.utils.BCD;
import ai.sangmado.gbprotocol.jt808.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.IVersionedSpecificationContext;
import ai.sangmado.gbprotocol.jt808.protocol.serialization.IJT808MessageBufferReader;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 基于 Netty ByteBuf 的 JT808 读取层实现
 */
public class JT808MessageNettyByteBufReader implements IJT808MessageBufferReader {
    private final IVersionedSpecificationContext ctx;
    private final ByteBuf buf;

    public JT808MessageNettyByteBufReader(IVersionedSpecificationContext ctx, ByteBuf buf) {
        this.ctx = ctx;
        this.buf = buf;
    }

    private boolean isBigEndian() {
        return ctx.getByteOrder() == ByteOrder.BIG_ENDIAN;
    }

    @Override
    public void markIndex() {
        buf.markReaderIndex();
    }

    @Override
    public void resetIndex() {
        buf.resetReaderIndex();
    }

    @Override
    public boolean isReadable() {
        return buf.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return buf.isReadable(size);
    }

    @Override
    public int readableBytes() {
        return buf.readableBytes();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @SuppressWarnings("IfStatementWithIdenticalBranches")
    @Override
    public int readWord() {
        if (isBigEndian()) {
            return (((buf.readByte() & 0xFF) << 8) | ((buf.readByte() & 0xFF))) & 0xFFFF;
        } else {
            return (((buf.readByte() & 0xFF)) | ((buf.readByte() & 0xFF) << 8)) & 0xFFFF;
        }
    }

    @Override
    public long readDWord() {
        if (isBigEndian()) {
            return (((buf.readByte() & 0xFF) << 24) | ((buf.readByte() & 0xFF) << 16) | ((buf.readByte() & 0xFF) << 8) | ((buf.readByte() & 0xFF))) & 0xFFFFFFFFL;
        } else {
            return (((buf.readByte() & 0xFF)) | ((buf.readByte() & 0xFF) << 8) | ((buf.readByte() & 0xFF) << 16) | ((buf.readByte() & 0xFF) << 24)) & 0xFFFFFFFFL;
        }
    }

    @Override
    public byte[] readBytes(int length) {
        byte[] x = new byte[length];
        buf.readBytes(x, 0, x.length);
        return x;
    }

    @Override
    public String readBCD(int length) {
        byte[] x = readBytes(length);
        return BCD.bcd2String(x);
    }

    @Override
    public String readString(int length) {
        byte[] x = readBytes(length);
        return ctx.getCharset().decode(ByteBuffer.wrap(x, 0, x.length)).toString();
    }

    @Override
    public String readStringRemaining() {
        return readString(buf.readableBytes());
    }
}
