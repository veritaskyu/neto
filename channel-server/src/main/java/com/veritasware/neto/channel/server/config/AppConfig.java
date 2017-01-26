package com.veritasware.neto.channel.server.config;

import com.veritasware.neto.model.RoomInfo;
import com.veritasware.neto.model.UserInfo;
import com.veritasware.neto.channel.server.service.RedisService;
import com.veritasware.neto.codec.binary.message.NetoJsonMessage;
import com.veritasware.neto.util.NetoResourceUtil;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Map;

/**
 * Created by chacker on 2016-09-19.
 */
@Configuration
public class AppConfig {

    static Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Value("${channelServer.charset}")
    private String charset;

    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.password}")
    private String redisPassword;

    @Value("${channelServer.maxFrameLength}")
    private int maxFrameLength;

    @Value("${channelServer.debug}")
    private boolean isDebug;

    @Value("${channelServer.unificationMode}")
    private boolean isUnificationMode;

    @Value("${channelServer.messagePackage}")
    private String messagePackage;

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private RedisService redisService;

    @Bean(name = "sslContext")
    public SslContext sslContext() {
        return null;
    }

    @Bean(name = "opcodeMap")
    public Map<Integer, Class<? extends NetoJsonMessage>> opcodeMap() throws Exception {
        return new NetoResourceUtil().opcodeMap("opcode.json", "com.veritasware.neto.codec.json.message");
    }

    @Scope("prototype")
    @Bean(name = "channelInitializer")
    public ChannelInboundHandlerAdapter channelInitializer() throws Exception {

//        return new ChannelInitializer<SocketChannel>() {
//            @Override
//            public void initChannel(SocketChannel ch) throws Exception {
//                ChannelPipeline pipeline = ch.pipeline();
//
//                boolean loggingHadler = java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().toString().indexOf("-agentlib:jdwp") > 0 || isDebug;
//
//                if (loggingHadler) {
//                    pipeline.addLast("logger", new LoggingHandler());
//                }
//
//                ProtocolUnificationHandler protocolUnificationHandler = new ProtocolUnificationHandler(isUnificationMode, opcodeMap(), sslContext(), maxFrameLength, charset);
//                protocolUnificationHandler.setRedisService(redisService);
//                pipeline.addLast(protocolUnificationHandler);
//            }
//        };

        ChannelServerInitializer channelServerInitializer = new ChannelServerInitializer();
        channelServerInitializer.setOpcodeMap(opcodeMap());
        channelServerInitializer.setRedisService(redisService);
        channelServerInitializer.setCharset(charset);
        channelServerInitializer.setSslCtx(sslContext());
        channelServerInitializer.setMaxFrameLength(8192);

        return channelServerInitializer;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory () {
        JedisConnectionFactory jedisFactory = new JedisConnectionFactory();
        jedisFactory.setPassword(redisPassword);
        jedisFactory.setHostName(redisHost);
        jedisFactory.setPort(redisPort);
        jedisFactory.setUsePool(true);
        return jedisFactory;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory);
        return stringRedisTemplate;
    }

    @Bean(name = "roomInfoJsonTemplate")
    public RedisTemplate roomInfoRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        return jsonRedisTemplate(jedisConnectionFactory, RoomInfo.class);
    }

    @Bean(name = "userInfoJsonTemplate")
    public RedisTemplate userInfoJsonTemplate(JedisConnectionFactory jedisConnectionFactory) {
        return jsonRedisTemplate(jedisConnectionFactory, UserInfo.class);
    }

    private RedisTemplate jsonRedisTemplate(JedisConnectionFactory jedisConnectionFactory, Class<?> clazz) {
        RedisTemplate template = new RedisTemplate();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer(clazz));
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new Jackson2JsonRedisSerializer(clazz));
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }

}
