package com.veritasware.neto.codec.json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chacker on 2016-10-05.
 */
public class NetoJsonStringToMapDecoder extends SimpleChannelInboundHandler<String> {

    static Logger logger = LoggerFactory.getLogger(NetoJsonStringToMapDecoder.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String errorOpcode;

    public NetoJsonStringToMapDecoder() {
        this("-1000");
    }
    public NetoJsonStringToMapDecoder(String errorOpcode) {
        this.errorOpcode = errorOpcode;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        Map<String, Object> data = null;

        try {

            data = objectMapper.readValue(msg, new TypeReference<Map<String, Object>>() {});

        } catch (Exception e) {
            data = new HashMap<String, Object>();
            data.put(errorOpcode, msg);
        }

        ctx.fireChannelRead(data);
    }
}
