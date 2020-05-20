package com.chopin.sunny.cluster.router.impl;

import com.chopin.sunny.cluster.router.Router;
import com.chopin.sunny.model.URL;


import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @title: angie
 * @description: 轮询加权
 * @author: sunxiaobo
 * @create: 2020-05-19 17:04
 **/
public class PollingWeightRouter implements Router {

    private int index=0;
    private Lock lock = new ReentrantLock();
    @Override
    public URL one(List<URL> servers) {
        if(servers.isEmpty()){
            return null;
        }
        if(servers.size()==0){
            return servers.get(0);
        }
        List<URL> list = addWeight(servers);
        lock.lock();
        if(index>=list.size()){
            index = 0;
        }
        URL server = list.get(index++);
        lock.unlock();
        if(server==null){
            server = list.get(0);
        }
        return server;
    }

    private List<URL> addWeight(List<URL> servers){
        return servers.stream().map(x-> IntStream.range(0,servers.size()).mapToObj(i->x.copy())).flatMap(x->x).collect(Collectors.toList());
    }
}
