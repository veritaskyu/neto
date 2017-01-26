package com.veritasware.neto.channel.server.config.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by chacker on 2016-09-19.
 */
@Data
@Component
@ConfigurationProperties(locations = "{application.yml}", prefix = "zookeeper")
public class ZookeeperProperty {
    private String host;
}
