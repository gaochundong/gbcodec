package ai.sangmado.gbcodec.jt808.codec;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JT808 协议解码器 参数配置
 */
@Getter
@Setter
@NoArgsConstructor
public class JT808MessageDecoderConfig {
    /**
     * 解码缓存长度
     */
    private int decodedBufferLength = 256;
}
