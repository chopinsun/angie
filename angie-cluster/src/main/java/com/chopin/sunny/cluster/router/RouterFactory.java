package com.chopin.sunny.cluster.router;

import com.chopin.sunny.cluster.router.impl.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-19 17:07
 **/
public class RouterFactory {

    private static final Map<RouterTypeEnum,Router> routers = new ConcurrentHashMap<>();
    private static final RouterTypeEnum DEFAULT_ROUTER_TYPE = RouterTypeEnum.Random;

    static {
        routers.put(RouterTypeEnum.Random, new RandomRouter());
        routers.put(RouterTypeEnum.RandomWithWeight,new RandomWeightRouter());
        routers.put(RouterTypeEnum.Polling,new PollingRouter());
        routers.put(RouterTypeEnum.PollingWithWeight,new PollingWeightRouter());
        routers.put(RouterTypeEnum.Hash,new HashRouter());
        routers.put(RouterTypeEnum.MinConnect,new MinConnectRouter());
    }

    public static Router getRouter(RouterTypeEnum type){
        if(type==null){
            return routers.get(DEFAULT_ROUTER_TYPE);
        }
        return routers.get(type);
    }

}
