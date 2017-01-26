package com.veritasware.neto.codec.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import com.veritasware.neto.codec.binary.message.NetoUnknownMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by chacker on 2016-10-27.
 */
public class NetoJsonToMessageDecoder extends SimpleChannelInboundHandler<String> {

    static Logger logger = LoggerFactory.getLogger(NetoJsonToMessageDecoder.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private final Map<Integer, Class<? extends NetoJsonMessage>> opcodeMap;

    public NetoJsonToMessageDecoder(Map<Integer, Class<? extends NetoJsonMessage>> opcodeMap) {
        this.opcodeMap = opcodeMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        NetoJsonMessage netoJsonMessage;

        try {

            netoJsonMessage = objectMapper.readValue(msg, NetoJsonMessage.class);

            Class<? extends NetoJsonMessage> decodeClass = opcodeMap.get(netoJsonMessage.getOpcode());

            if (decodeClass != null) {
                netoJsonMessage = objectMapper.readValue(msg, decodeClass);
            } else {
                netoJsonMessage = new NetoUnknownMessage(msg);
            }
        } catch (Exception e) {
            netoJsonMessage = new NetoUnknownMessage(msg);
        }

        ctx.fireChannelRead(netoJsonMessage);
    }

}
