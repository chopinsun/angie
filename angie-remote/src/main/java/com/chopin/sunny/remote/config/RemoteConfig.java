package com.chopin.sunny.remote.config;

import com.chopin.sunny.remote.netty.NettyChannelPoolFactroy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-19 19:29
 **/
@Configuration
public class RemoteConfig {

    @Bean
    public void initNettyChannelPool(){
        NettyChannelPoolFactroy.getInstance().init();
    }

}
