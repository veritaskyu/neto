package com.veritasware.neto.session.server;

import com.veritasware.neto.session.server.service.ZookeeperService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by chacker on 2016-09-19.
 */
@SpringBootApplication
@ComponentScan
@Configuration
public class SessionServerStarter {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(SessionServerStarter.class, args);
        ZookeeperService bean = ctx.getBean(ZookeeperService.class);
        bean.run();
    }
}
