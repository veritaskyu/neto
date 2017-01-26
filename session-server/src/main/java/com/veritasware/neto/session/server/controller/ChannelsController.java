package com.veritasware.neto.session.server.controller;

import com.veritasware.neto.session.server.model.ChannelInfo;
import com.veritasware.neto.session.server.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by chacker on 2016-09-23.
 */
@RestController
@RequestMapping("/channels")
public class ChannelsController {

    private static Logger logger = LoggerFactory.getLogger(ChannelsController.class);

    @Autowired
    private RedisService redisService;


    @RequestMapping
    @ResponseBody
    public List<ChannelInfo> allChannels() {
        return redisService.getChannels();
    }

    @RequestMapping("/{roomId}")
    @ResponseBody
    public ChannelInfo channel(@PathVariable("roomId") String roomId) {
        ChannelInfo channel = redisService.getChannel(roomId);
        return channel;
    }
}
