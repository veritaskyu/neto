package com.veritasware.neto.session.server.config;

import com.veritasware.neto.session.server.model.ChannelInfo;
import com.veritasware.neto.session.server.util.ConsistentHashing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chacker on 2016-09-21.
 */
@Configuration
public class SessionServerConfig {

    @Bean(name = "beforeChannels")
    public List<ChannelInfo> beforeChannels() {
        return new ArrayList<ChannelInfo>();
    }

    @Bean(name = "consistentHashingMap")
    public ConsistentHashing<ChannelInfo> consistentHashingMap() {
        return new ConsistentHashing<ChannelInfo>();
    }
}
