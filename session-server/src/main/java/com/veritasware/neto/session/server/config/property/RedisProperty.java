package com.veritasware.neto.session.server.config.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by chacker on 2016-09-19.
 */
@Data
@Component
@ConfigurationProperties(locations = "{application.yml}", prefix = "redis")
public class RedisProperty {
    private String host;
    private int port;
    private String password;
}
