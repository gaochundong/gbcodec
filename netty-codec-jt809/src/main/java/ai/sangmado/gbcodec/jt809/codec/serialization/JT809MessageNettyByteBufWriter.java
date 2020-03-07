package ai.sangmado.gbcodec.jt809.codec.serialization;

import ai.sangmado.gbprotocol.gbcommon.utils.BCD;
import ai.sangmado.gbprotocol.jt809.protocol.ISpecificationContext;
import ai.sangmado.gbprotocol.jt809.protocol.serialization.IJT809MessageBufferWriter;
import com.google.common.primitives.UnsignedLong;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import static ai.sangmado.gbprotocol.gbcommon.utils.Bits.*;

/**
 * 基于 Netty ByteBuf 的 JT809 写入层实现
 */
public class JT809MessageNettyByteBufWriter implements IJT809MessageBufferWriter {
    private ISpecificationContext ctx;
    private ByteBuf buf;

    public JT809MessageNettyByteBufWriter(ISpecificationContext ctx, ByteBuf buf) {
        this.ctx = ctx;
        this.buf = buf;
    }

    private boolean isBigEndian() {
        return ctx.getByteOrder() == ByteOrder.BIG_ENDIAN;
    }

    @Override
    public void writeByte(byte x) {
        buf.writeByte(x);
    }

    @Override
    public void writeByte(int x) {
        writeByte(int0(x));
    }

    @Override
    public void writeUInt16(int x) {
        if (isBigEndian()) {
            buf.writeByte(int1(x));
            buf.writeByte(int0(x));
        } else {
            buf.writeByte(int0(x));
            buf.writeByte(int1(x));
        }
    }

    @Override
    public void writeUInt32(long x) {
        if (isBigEndian()) {
            buf.writeByte(long3(x));
            buf.writeByte(long2(x));
            buf.writeByte(long1(x));
            buf.writeByte(long0(x));
        } else {
            buf.writeByte(long0(x));
            buf.writeByte(long1(x));
            buf.writeByte(long2(x));
            buf.writeByte(long3(x));
        }
    }

    @Override
    public void writeUInt64(UnsignedLong x) {
        if (isBigEndian()) {
            buf.writeByte(long7(x.longValue()));
            buf.writeByte(long6(x.longValue()));
            buf.writeByte(long5(x.longValue()));
            buf.writeByte(long4(x.longValue()));
            buf.writeByte(long3(x.longValue()));
            buf.writeByte(long2(x.longValue()));
            buf.writeByte(long1(x.longValue()));
            buf.writeByte(long0(x.longValue()));
        } else {
            buf.writeByte(long0(x.longValue()));
            buf.writeByte(long1(x.longValue()));
            buf.writeByte(long2(x.longValue()));
            buf.writeByte(long3(x.longValue()));
            buf.writeByte(long4(x.longValue()));
            buf.writeByte(long5(x.longValue()));
            buf.writeByte(long6(x.longValue()));
            buf.writeByte(long7(x.longValue()));
        }
    }

    @Override
    public void writeBytes(byte[] x) {
        writeBytes(x, 0, x.length);
    }

    @Override
    public void writeBytes(byte[] x, int offset, int length) {
        buf.writeBytes(x, offset, length);
    }

    @Override
    public void writeBytes(ByteBuffer x) {
        buf.writeBytes(x);
    }

    @Override
    public void writeBCD(String x) {
        if (x == null)
            throw new IllegalArgumentException("input string cannot be null.");
        writeBytes(BCD.string2BCD(x));
    }

    @Override
    public void writeString(String x) {
        if (x == null)
            throw new IllegalArgumentException("input string cannot be null.");
        writeBytes(x.getBytes(ctx.getCharset()));
    }
}
