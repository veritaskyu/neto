package com.veritasware.neto.codec.binary.message;

import com.veritasware.neto.codec.binary.message.NetoInboundByteMessage;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * Created by chacker on 2016-03-22.
 */
public class NetoUnknownInboundByteMessage extends NetoInboundByteMessage {

    private String message;

    public NetoUnknownInboundByteMessage(ByteBuf buffer) {
        super(buffer);
    }

    public NetoUnknownInboundByteMessage(String message, ByteBuf buffer) {
        super(buffer);
        this.message = message;
    }

    @Override
    protected void readPackets(Charset charset) {}
}
