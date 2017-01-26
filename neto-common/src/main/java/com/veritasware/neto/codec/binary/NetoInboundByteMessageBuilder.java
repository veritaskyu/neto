package com.veritasware.neto.codec.binary;

import com.veritasware.neto.codec.binary.message.NetoInboundByteMessage;
import io.netty.buffer.ByteBuf;

/**
 * Created by chacker on 2016-02-17.
 */
public interface NetoInboundByteMessageBuilder {
    NetoInboundByteMessage create(int commandCode, ByteBuf buff) throws Exception ;
}
