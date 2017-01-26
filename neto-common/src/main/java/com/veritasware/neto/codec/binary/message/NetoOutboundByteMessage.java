package com.veritasware.neto.codec.binary.message;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.charset.Charset;

/**
 * Created by chacker on 2016-02-16.
 */
public abstract class NetoOutboundByteMessage {

    protected final int commandCode;
    protected ByteBuf buffer;

    public NetoOutboundByteMessage(int commandCode) {
        this(commandCode, Unpooled.buffer());
    }

    public NetoOutboundByteMessage(int commandCode, ByteBuf buffer) {
        this.commandCode = commandCode;
        if (buffer == null) {
            this.buffer = buffer;
        } else {
            this.buffer = Unpooled.buffer();
        }
    }

    protected void writeByte(int value) {
        buffer.writeByte(value);
    }

    protected void writeShort(int value) {
        buffer.writeShort(value);
    }

    protected void writeBytes(byte[] value, int length) {
        buffer.writeBytes(value, 0, length);
    }

    protected void writeInt(int value) {
        buffer.writeInt(value);
    }

    protected void writeLong(long value) {
        buffer.writeLong(value);
    }

    protected void writeString(String str, Charset charset) {
        byte[] strBytes = null;

        if (str != null && (strBytes = str.getBytes(charset)) != null) {
            writeInt(strBytes.length + 1);
            writeBytes(strBytes, strBytes.length);
            writeByte(0);
        } else {
            writeInt(1);
            writeByte(0);
        }
    }

    public ByteBuf buildMessage(Charset charset) {
        writeInt(commandCode); // 커맨드 코드
        buffer.markWriterIndex();
        writeInt(0); // data 크기 넣을 자리 예약
        int headerLength = buffer.writerIndex();
        writePacket(charset);
        int wIndex = buffer.writerIndex();
        int length = wIndex - headerLength;
        buffer.resetWriterIndex();
        writeInt(length);
        buffer.writerIndex(wIndex);
        return buffer;
    }

    protected abstract void writePacket(Charset charset);

}
