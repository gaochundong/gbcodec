package ai.sangmado.gbcodec.jt809.codec.serialization;

import ai.sangmado.gbprotocol.gbcommon.utils.BCD;
import ai.sangmado.gbprotocol.gbcommon.utils.Bits;
import ai.sangmado.gbprotocol.jt809.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.serialization.IJT809MessageBufferReader;
import com.google.common.primitives.UnsignedLong;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * 基于 Netty ByteBuf 的 JT809 读取层实现
 */
public class JT809MessageNettyByteBufReader implements IJT809MessageBufferReader {
    private ISpecificationContext ctx;
    private ByteBuf buf;

    public JT809MessageNettyByteBufReader(ISpecificationContext ctx, ByteBuf buf) {
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
    public int readableBytes() {
        return buf.readableBytes();
    }

    @Override
    public byte readByte() {
        return buf.readByte();
    }

    @SuppressWarnings("IfStatementWithIdenticalBranches")
    @Override
    public int readUInt16() {
        if (isBigEndian()) {
            return (((buf.readByte() & 0xFF) << 8) | ((buf.readByte() & 0xFF))) & 0xFFFF;
        } else {
            return (((buf.readByte() & 0xFF)) | ((buf.readByte() & 0xFF) << 8)) & 0xFFFF;
        }
    }

    @Override
    public long readUInt32() {
        if (isBigEndian()) {
            return (((buf.readByte() & 0xFF) << 24) | ((buf.readByte() & 0xFF) << 16) | ((buf.readByte() & 0xFF) << 8) | ((buf.readByte() & 0xFF))) & 0xFFFFFFFFL;
        } else {
            return (((buf.readByte() & 0xFF)) | ((buf.readByte() & 0xFF) << 8) | ((buf.readByte() & 0xFF) << 16) | ((buf.readByte() & 0xFF) << 24)) & 0xFFFFFFFFL;
        }
    }


    @Override
    public UnsignedLong readUInt64() {
        byte b0 = buf.readByte();
        byte b1 = buf.readByte();
        byte b2 = buf.readByte();
        byte b3 = buf.readByte();
        byte b4 = buf.readByte();
        byte b5 = buf.readByte();
        byte b6 = buf.readByte();
        byte b7 = buf.readByte();
        if (isBigEndian()) {
            return UnsignedLong.fromLongBits(Bits.makeLong(b0, b1, b2, b3, b4, b5, b6, b7));
        } else {
            return UnsignedLong.fromLongBits(Bits.makeLong(b7, b6, b5, b4, b3, b2, b1, b0));
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
