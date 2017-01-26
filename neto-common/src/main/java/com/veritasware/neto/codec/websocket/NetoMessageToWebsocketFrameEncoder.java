package com.veritasware.neto.codec.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by chacker on 2016-10-20.
 */
@Deprecated
public class NetoMessageToWebsocketFrameEncoder extends MessageToMessageEncoder<NetoJsonMessage> {

    static Logger logger = LoggerFactory.getLogger(NetoMessageToWebsocketFrameEncoder.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void encode(ChannelHandlerContext ctx, NetoJsonMessage msg, List<Object> out) throws Exception {

        String content = objectMapper.writeValueAsString(msg);

        logger.error("{}", content);

        out.add(new TextWebSocketFrame(content));
    }

}
