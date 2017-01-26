package com.veritasware.neto.channel.server.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

/**
 * Created by chacker on 2016-09-19.
 */
@Data
@Component
@ConfigurationProperties(locations = "{application.yml}", prefix = "channelServer")
public class SocketServerProperty {
    private String hostIp;
    private boolean bindPortRandom;
    private int bossThreadCount;
    private int workerThreadCount;
    private int bindPort;
    private Charset charset;
    private long idleTimeoutMilliSeconds;
}
