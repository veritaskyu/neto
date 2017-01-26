package com.veritasware.neto.session.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;
import java.util.Set;

/**
 * Created by chacker on 2016-09-21.
 */
@Data
@ToString(of = {"host", "port"})
@EqualsAndHashCode(of = {"host", "port"})
public class ChannelInfo {
    private String host;
    private int port;
    private int replica;
    private String owner;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<String> users;

    private long userCount;
}
