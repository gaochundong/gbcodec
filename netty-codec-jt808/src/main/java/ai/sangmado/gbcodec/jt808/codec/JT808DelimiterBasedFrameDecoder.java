package ai.sangmado.gbcodec.jt808.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;

/**
 * JT808 消息帧分割器
 */
public class JT808DelimiterBasedFrameDecoder extends DelimiterBasedFrameDecoder {

    /**
     * 消息帧分隔符
     */
    public static byte JT808_FRAME_DELIMITER = 0x7e;
    /**
     * 消息帧最大长度
     */
    public static int JT808_MAX_FRAME_LENGTH = 2048;
    
    public JT808DelimiterBasedFrameDecoder() {
        this(JT808_MAX_FRAME_LENGTH, false, true, Unpooled.wrappedBuffer(new byte[]{JT808_FRAME_DELIMITER}));
    }

    public JT808DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf delimiter) {
        super(maxFrameLength, stripDelimiter, failFast, delimiter);
    }
}
