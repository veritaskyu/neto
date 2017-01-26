package com.veritasware.neto.codec.binary.message;

import com.veritasware.neto.exception.PacketReadException;
import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;

/**
 * InboundMessage의 기본이 되는 추상 클래스 <br>
 * 해당 클래스를 상속 받아서 readPackets() Override하여 사용한다. <br>
 *
 * Created by chacker on 2016-02-16.
 */
public abstract class NetoInboundByteMessage {

    private final ByteBuf buffer;

    /**
     * @param buffer MessageDecoder로 부터 읽어낸 ByteBuf
     */
    protected NetoInboundByteMessage(ByteBuf buffer) {
        this.buffer = buffer;
    }

    /**
     * @return Buffer로부터 1byte 만큼 읽어서 반환한다.
     */
    protected byte readByte() {
        return this.buffer.readByte();
    }

    /**
     * @return Buffer로부터 2byte 만큼 읽어서 반환한다.
     */
    protected short readShort() {
        return this.buffer.readShort();
    }

    /**
     * @return Buffer로부터 int 크기 만큼 읽어서 반환한다.
     */
    protected int readInt() {
        return this.buffer.readInt();
    }

    /**
     * @return Buffer로부터 long 크기 만큼 읽어서 반환한다.
     */
    protected long readLong() {
        return this.buffer.readLong();
    }

    /**
     * @param length Buffer로 부터 읽어 낼 길이
     * @return 주어진 길이 만큼 Buffer로 부터 읽은 후 byte array 반환
     */
    protected byte[] readBytes(int length) {
        byte[] bytes = new byte[length];
        buffer.readBytes(bytes);
        return bytes;
    }

    /**
     * @param charset 디코딩할 캐릭터셋
     * @return Buffer로 부터 읽어낸 문자열
     */
    protected String readString(Charset charset) {
        // 문자열은 다음과 같은 형태로 구성된다.<br>
        // | length (4byte) |  data |<br>

        int len = readInt(); // 문자열의 길이 -1(null padding 포함)

        if (len < 2) { // null padding을 포함하여 최소 문자열 길이는 2.
            return null;
        }

        byte[] bytes = new byte[len];
        int readerIndex = buffer.readerIndex();
        buffer.getBytes(readerIndex, bytes);
        String s = new String(bytes, 0, len - 1 /* null padding */, charset);
        buffer.readerIndex(readerIndex + s.getBytes(charset).length + 1 /* null padding */);
        return s;
    }

    /**
     * readPackets()을 통해 멤버 데이터를 구성하고 버퍼를 해제한다.
     * @param charset 디코딩할 캐릭터셋
     * @return 멤버 데이터가 구성된 메시지
     */
    public NetoInboundByteMessage buildMessage(Charset charset) {
        try {
            readPackets(charset);
        } catch (Exception e) {
            throw new PacketReadException(e);
        } finally {
            buffer.release();
        }
        return this;
    }

    /**
     * Buffer 에서 읽어야할 데이터를 순서대로 정의한다.<br>
     * ex)
     *<pre>
     * protected void readPackets(Charset charset) {
     *     int no = readInt();
     *     String str = readString(charset);
     * }
     * </pre>
     * @param charset 디코딩할 캐릭터셋
     */
    protected abstract void readPackets(Charset charset);
}
