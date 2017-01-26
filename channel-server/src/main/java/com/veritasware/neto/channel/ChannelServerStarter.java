package com.veritasware.neto.channel;

import com.veritasware.neto.channel.server.SocketChannelServer;
import com.veritasware.neto.channel.server.config.prop.SocketServerProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by chacker on 2016-09-19.
 */

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan
public class ChannelServerStarter {

    static Logger logger = LoggerFactory.getLogger(ChannelServerStarter.class);

    public static void main(String[] args) throws IOException {

        ConfigurableApplicationContext context = SpringApplication.run(ChannelServerStarter.class, args);

        // 현재 실행된 서버의 공인아이피 조회
        String hostCheckUrl = context.getEnvironment().getProperty("channelServer.hostCheckUrl");

        // 서버 설정 application.yml에서 로드
        SocketServerProperty socketServerProperty = context.getBean(SocketServerProperty.class);

        String serverHost;

        try {
            serverHost = new RestTemplate().getForObject(hostCheckUrl, String.class);
            socketServerProperty.setHostIp(serverHost);
        } catch (Exception e) {
            logger.error("Not Found Host : check url - {}", hostCheckUrl);
            System.exit(-1);
        }

        // 랜덤포트로 설정 되어있다면 가용한 포트를 할당
        if (socketServerProperty.isBindPortRandom()) {
            ServerSocket s = new ServerSocket(0);
            s.setReuseAddress(true);
            socketServerProperty.setBindPort(s.getLocalPort());
            s.close();
        } else {
            socketServerProperty.setBindPort(socketServerProperty.getBindPort());
        }

        SocketChannelServer socketChannelServer = context.getBean(SocketChannelServer.class);
        socketChannelServer.run();
    }

}
