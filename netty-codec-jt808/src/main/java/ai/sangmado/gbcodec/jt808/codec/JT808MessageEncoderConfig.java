package ai.sangmado.gbcodec.jt808.codec;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JT808 协议编码器 参数配置
 */
@Getter
@Setter
@NoArgsConstructor
public class JT808MessageEncoderConfig {

    /**
     * 编码缓存长度
     */
    private int encodedBufferLength = 256;
}
