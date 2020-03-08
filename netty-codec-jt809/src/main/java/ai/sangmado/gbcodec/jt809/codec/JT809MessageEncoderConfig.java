package ai.sangmado.gbcodec.jt809.codec;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JT809 协议编码器 参数配置
 */
@Getter
@Setter
@NoArgsConstructor
public class JT809MessageEncoderConfig {

    /**
     * 编码缓存长度
     */
    private int encodedBufferLength = 256;
}
