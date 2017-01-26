package com.veritasware.neto.codec.websocket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.veritasware.neto.codec.binary.message.NetoExceptionMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by chacker on 2016-10-05.
 */
@Deprecated
public class NetoJsonStringToMapWebSocketDecoder extends SimpleChannelInboundHandler<WebSocketFrame> {

    static Logger logger = LoggerFactory.getLogger(NetoJsonStringToMapWebSocketDecoder.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {

        NetoExceptionMessage exceptionMessage;

        Map<String, Object> data = null;

        try {

            logger.error("{}", frame);

            if (frame instanceof PingWebSocketFrame) {
                ctx.write(new PongWebSocketFrame(frame.content().retain()));
                return;
            }

            if (frame instanceof BinaryWebSocketFrame) {
                // Echo the frame
                ctx.write(frame.retain());
                return;
            }

            if (frame instanceof TextWebSocketFrame) {
                data = objectMapper.readValue(((TextWebSocketFrame) frame).text(), new TypeReference<Map<String, Object>>() {});
            } else {
                exceptionMessage = new NetoExceptionMessage("Unsupported : " + frame.getClass().getName());
                data = objectMapper.convertValue(exceptionMessage, Map.class);
            }
        } catch (Exception e) {
            exceptionMessage = new NetoExceptionMessage(e.getMessage());
            data = objectMapper.convertValue(exceptionMessage, Map.class);
        }

        ctx.fireChannelRead(data);
    }
}
