package com.veritasware.neto.test.model;

import com.veritasware.neto.codec.binary.message.NetoInboundByteMessage;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * Created by chacker on 2016-08-11.
 */
public class TestInboundByteMessage extends NetoInboundByteMessage {

    private byte byteMsg;
    private short shortMsg;
    private int intMsg;
    private long longMsg;
    private byte[] bytesMsg;
    private String stringMessage;

    public TestInboundByteMessage(ByteBuf buffer) {
        super(buffer);
    }

    public byte getByteMsg() {
        return byteMsg;
    }

    public short getShortMsg() {
        return shortMsg;
    }

    public int getIntMsg() {
        return intMsg;
    }

    public long getLongMsg() {
        return longMsg;
    }

    public byte[] getBytesMsg() {
        return bytesMsg;
    }

    public String getStringMessage() {
        return stringMessage;
    }

    @Override
    protected void readPackets(Charset charset) {
        readMetaData();
        byteMsg = readByte();
        shortMsg = readShort();
        intMsg = readInt();
        longMsg = readLong();
        stringMessage = readString(charset);
        int byteLen = readInt();
        bytesMsg = readBytes(byteLen);
    }

    public void readMetaData() {
        readInt(); // Command Code
        readInt(); // Data Length
    }
}