package com.chopin.sunny.registry.api;

import com.chopin.sunny.model.URL;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface Registry {
    //key:接口，value:服务列表
    Map<String, Set<URL>> localRegisterCaches = new ConcurrentHashMap<>();
    //key:服务，value：订阅列表
    Map<URL, Set<URL>> localSubscribeCaches = new ConcurrentHashMap<>();
    void register(URL url);
    void unregister(URL url);
    void subscribe(URL url, Set<URL> producter);
    void unSubscribe(URL url, Set<URL> producter);

    void init();
    void destroy();

    default Map<String,Set<URL>> getLocalRegisterCaches(){
        return localRegisterCaches;
    }

    default Map<URL, Set<URL>> getLocalSubscribeCaches(){
        return localSubscribeCaches;
    }
}
