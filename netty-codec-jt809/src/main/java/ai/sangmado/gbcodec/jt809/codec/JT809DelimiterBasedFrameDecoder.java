package ai.sangmado.gbcodec.jt809.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

/**
 * JT809 消息帧分割器
 */
public class JT809DelimiterBasedFrameDecoder extends DelimiterBasedFrameDecoder {

    /**
     * 消息帧分隔符
     */
    public static byte JT809_FRAME_DELIMITER = 0x7e;
    /**
     * 消息帧最大长度
     */
    public static int JT809_MAX_FRAME_LENGTH = 2048;

    public JT809DelimiterBasedFrameDecoder() {
        this(JT809_MAX_FRAME_LENGTH, false, true, Unpooled.wrappedBuffer(new byte[]{JT809_FRAME_DELIMITER}));
    }

    public JT809DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf delimiter) {
        super(maxFrameLength, stripDelimiter, failFast, delimiter);
    }
}
