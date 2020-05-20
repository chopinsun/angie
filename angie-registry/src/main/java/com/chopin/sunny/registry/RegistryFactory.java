package com.chopin.sunny.registry;

import com.chopin.sunny.registry.api.Registry;
import com.chopin.sunny.registry.redis.RedisRegistry;
import com.chopin.sunny.registry.zookeeper.ZkRegistry;
import com.chopin.sunny.utils.PropertyConfigeHelper;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-15 16:51
 **/
@Log4j2
public class RegistryFactory {

    private static Map<RegistType,Registry> registries  = new HashMap<>();
    private static final String registryType =(String) PropertyConfigeHelper.getProperty("angie.registry.type");
    private static RegistType DEFAULT_TYPE=RegistType.ZOOKEEPER;

    static {
        registries.put(RegistType.ZOOKEEPER, new ZkRegistry());
        registries.put(RegistType.REDIS,new RedisRegistry());
    }

    public static Registry getRegistry(){
        if(registryType!=null){
            return registries.get(registryType);
        }
        return registries.get(DEFAULT_TYPE);
    }

    public static void releace(){
        registries.values().stream().forEach(x->x.destroy());
    }


    public enum  RegistType{
        ZOOKEEPER("zookeeper"),
        REDIS("redis"),
        ;
        private String name;

        RegistType(String name) {
            this.name = name;
        }

    }
}
