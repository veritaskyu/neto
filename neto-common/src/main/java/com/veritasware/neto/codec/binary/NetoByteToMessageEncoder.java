package com.veritasware.neto.codec.binary;

import com.veritasware.neto.codec.binary.message.NetoOutboundByteMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * 메시지 인코딩을 담당하는 클래스 <br>
 * 패킷 구성 : | Command | Data Length | Data | <br>
 * Created by chacker on 2016-02-16.
 */
public class NetoByteToMessageEncoder extends MessageToByteEncoder<NetoOutboundByteMessage>{

    static Logger logger = LoggerFactory.getLogger(NetoByteToMessageEncoder.class);

    private final Charset charset;

    /**
     *
     * @param charset 캐릭터셋
     */
    public NetoByteToMessageEncoder(Charset charset) {
        this.charset = charset;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, NetoOutboundByteMessage msg, ByteBuf out) throws Exception {
        ByteBuf buffer = msg.buildMessage(charset);
        out.writeBytes(buffer);
        logger.debug("encode - {}", buffer);
        buffer.release();
    }
}
