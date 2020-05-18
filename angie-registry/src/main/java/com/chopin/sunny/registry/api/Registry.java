package com.chopin.sunny.registry.api;

import com.chopin.sunny.model.URL;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public interface Registry {
    Map<String, URL> localRegisterCaches = new ConcurrentHashMap<>();
    Map<URL, Set<URL>> localSubscribeCaches = new ConcurrentHashMap<>();
    void register(URL url);
    void unregister(URL url);
    void subscribe(URL url, Set<URL> producter);
    void unSubscribe(URL url, Set<URL> producter);

    void destroy();

    default Map<String,URL> getLocalRegisterCaches(){
        return localRegisterCaches;
    }

    default Map<URL, Set<URL>> getLocalSubscribeCaches(){
        return localSubscribeCaches;
    }
}
