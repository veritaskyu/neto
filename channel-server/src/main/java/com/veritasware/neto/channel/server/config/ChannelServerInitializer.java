package com.veritasware.neto.channel.server.config;

import com.google.common.collect.BiMap;
import com.veritasware.neto.channel.server.handler.MessageHandler;
import com.veritasware.neto.channel.server.service.RedisService;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import com.veritasware.neto.codec.json.NetoJsonToMessageDecoder;
import com.veritasware.neto.codec.json.NetoMessageToJsonEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.ssl.SslContext;
import lombok.Getter;
import lombok.Setter;

import java.nio.charset.Charset;
import java.util.Map;


/**
 * Created by chacker on 2016-11-04.
 */
@Getter
@Setter
public class ChannelServerInitializer extends ChannelInitializer<SocketChannel> {

    private Map<Integer, Class<? extends NetoJsonMessage>> opcodeMap;
    private int maxFrameLength = 8192;
    private RedisService redisService;
    private SslContext sslCtx;
    private String charset;


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        NetoJsonToMessageDecoder decoder = new NetoJsonToMessageDecoder(opcodeMap);
        NetoMessageToJsonEncoder encoder = new NetoMessageToJsonEncoder();

        if (opcodeMap instanceof BiMap) {
            encoder.setOpcodeMap((BiMap<Integer, Class<? extends NetoJsonMessage>>) opcodeMap);
        }

        MessageHandler handler = new MessageHandler(redisService);

        ChannelPipeline p = ch.pipeline();

        p.addLast(new DelimiterBasedFrameDecoder(maxFrameLength, Delimiters.lineDelimiter()));
        p.addLast(new StringDecoder(Charset.forName(charset)));
        p.addLast(decoder);
        p.addLast(encoder);
        p.addLast(handler);
    }

}
