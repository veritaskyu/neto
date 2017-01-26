package com.veritasware.neto.test.model;

import com.veritasware.neto.codec.binary.message.NetoOutboundByteMessage;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * Created by chacker on 2016-08-11.
 */
public class TestOutboundByteMessage extends NetoOutboundByteMessage {

    private byte byteMsg;
    private short shortMsg;
    private int intMsg;
    private long longMsg;
    private byte[] bytesMsg;
    private String stringMessage;

    private Object nullMessage;

    public TestOutboundByteMessage(int commandCode, ByteBuf buffer) {
        super(commandCode, buffer);
    }

    public void setByteMsg(byte byteMsg) {
        this.byteMsg = byteMsg;
    }

    public void setShortMsg(short shortMsg) {
        this.shortMsg = shortMsg;
    }

    public void setIntMsg(int intMsg) {
        this.intMsg = intMsg;
    }

    public void setLongMsg(long longMsg) {
        this.longMsg = longMsg;
    }

    public void setBytesMsg(byte[] bytesMsg) {
        this.bytesMsg = bytesMsg;
    }

    public void setStringMessage(String stringMessage) {
        this.stringMessage = stringMessage;
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
    protected void writePacket(Charset charset) {
        writeByte(byteMsg);
        writeShort(shortMsg);
        writeInt(intMsg);
        writeLong(longMsg);
        writeString(stringMessage, charset);
        writeInt(bytesMsg.length);
        writeBytes(bytesMsg, bytesMsg.length);
    }

}
