package ai.sangmado.gbcodec.jt809.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.TooLongFrameException;

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

    /**
     * Creates a new instance.
     */
    public JT809DelimiterBasedFrameDecoder() {
        this(JT809_MAX_FRAME_LENGTH, false, true, Unpooled.wrappedBuffer(new byte[]{JT809_FRAME_DELIMITER}));
    }

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength the maximum length of the decoded frame.
     *                       A {@link TooLongFrameException} is thrown if
     *                       the length of the frame exceeds this value.
     * @param stripDelimiter whether the decoded frame should strip out the
     *                       delimiter or not
     * @param failFast       If <tt>true</tt>, a {@link TooLongFrameException} is
     *                       thrown as soon as the decoder notices the length of the
     *                       frame will exceed <tt>maxFrameLength</tt> regardless of
     *                       whether the entire frame has been read.
     *                       If <tt>false</tt>, a {@link TooLongFrameException} is
     *                       thrown after the entire frame that exceeds
     *                       <tt>maxFrameLength</tt> has been read.
     * @param delimiter      the delimiter
     */
    public JT809DelimiterBasedFrameDecoder(int maxFrameLength, boolean stripDelimiter, boolean failFast, ByteBuf delimiter) {
        super(maxFrameLength, stripDelimiter, failFast, delimiter);
    }
}
