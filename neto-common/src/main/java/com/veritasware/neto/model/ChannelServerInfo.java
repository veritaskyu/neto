package com.veritasware.neto.model;

import lombok.Data;

/**
 * Created by chacker on 2016-09-21.
 */
@Data
public class ChannelServerInfo {
    private String host;
    private int port;
    private int replica;
}
