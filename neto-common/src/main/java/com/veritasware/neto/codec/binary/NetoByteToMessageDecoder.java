package com.veritasware.neto.codec.binary;

import com.veritasware.neto.codec.binary.message.NetoInboundByteMessage;
import com.veritasware.neto.codec.binary.message.NetoUnknownInboundByteMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 메시지 디코딩을 담당하는 클래스 <br>
 * 패킷 구성 : | Command | Data Length | Data | <br>
 * Created by chacker on 2016-02-16.
 */
public class NetoByteToMessageDecoder extends ReplayingDecoder<NetoByteToMessageDecoder.DecodingState> {

    private Logger logger = LoggerFactory.getLogger(NetoByteToMessageDecoder.class);

    private final int dataLengthMaximum;

    enum DecodingState {
        COMMAND_CODE, DATA_LENGTH, DATA
    }

    private final NetoInboundByteMessageBuilder inboundByteMessageBuilder;
    private final Charset charset;

    private int commandCode;
    private int dataLength;

    /**
     * @param inboundByteMessageBuilder CommandCode에 따라 InboundMessage를 생성/반환하는 인스턴스
     * @param charset 캐릭터셋
     * @param dataLengthMaximum 채널로 부터 최대로 읽을 본문 크기
     */
    public NetoByteToMessageDecoder(NetoInboundByteMessageBuilder inboundByteMessageBuilder, Charset charset, int dataLengthMaximum) {
        super(DecodingState.COMMAND_CODE);
        this.inboundByteMessageBuilder = inboundByteMessageBuilder;
        this.charset = charset;
        this.dataLengthMaximum = dataLengthMaximum;
    }

    /**
     *
     * @param inboundByteMessageBuilder CommandCode에 따라 InboundMessage를 생성/반환하는 인스턴스
     * @param charset 캐릭터셋
     */
    public NetoByteToMessageDecoder(NetoInboundByteMessageBuilder inboundByteMessageBuilder, Charset charset) {
        this(inboundByteMessageBuilder, charset, 65535);
    }

    /**
     * 채널로부터 수신된 버퍼에서 Message를 Decode한다.
     * @param ctx 채널 컨텍스트
     * @param in 수신된 버퍼
     * @param out pipeline상 다음 Handler로 전달할 객체를 담는 collections
     * @throws Exception 던질 예외
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        DecodingState state = state();

        switch (state) {
            case COMMAND_CODE:
                this.commandCode = in.readInt();
                logger.debug("DECODE STATE:COMMAND_CODE - {}", commandCode);
                checkpoint(DecodingState.DATA_LENGTH);
                break;
            case DATA_LENGTH:
                this.dataLength = in.readInt();
                logger.debug("DECODE STATE:DATA_LENGTH - {}", this.dataLength);
                if (this.dataLength > 0 && this.dataLength < dataLengthMaximum) {
                    checkpoint(DecodingState.DATA);
                    break;
                }
                throw new Exception("Invalid Length : " + dataLength);
            case DATA:
                ByteBuf buff = in.readBytes(this.dataLength);
                logger.debug("DECODE STATE:DATA - {}", buff);
                NetoInboundByteMessage message = decodeData(commandCode, buff, charset);
                if (message != null) {
                    out.add(message);
                }
                checkpoint(DecodingState.COMMAND_CODE);
                break;
            default:
                throw new Exception("Unknown Decodinig state : " + state);
        }
    }

    /**
     * 채널로부터 수신된 버퍼에서 data를 발신측에서 보낸 순서대로 Decode하고 <br>
     * MessageHandler로(pipeline에서 다음 핸들러) 전달할 {@link NetoInboundByteMessage}를 {@link NetoInboundByteMessageBuilder} 를 통해 생성하여 반환한다.
     *
     * @param commandCode 커맨드코드
     * @param buff 채널로부터 수신되는 버퍼
     * @param charset 캐릭터셋
     * @return MessageHandler로 전달 시킬 NetoInboundByteMessage
     */
    protected NetoInboundByteMessage decodeData(int commandCode, ByteBuf buff, Charset charset) {
        NetoInboundByteMessage decodeMessage = null;
        try {
            decodeMessage = inboundByteMessageBuilder.create(commandCode, buff);
            decodeMessage.buildMessage(charset);
        } catch (Exception e) {
            decodeMessage = new NetoUnknownInboundByteMessage(e.getMessage(), buff);
        }
        return decodeMessage;
    }


}
