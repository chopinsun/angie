package com.chopin.sunny.cluster.router.impl;

import com.chopin.sunny.cluster.router.Router;
import com.chopin.sunny.model.URL;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @title: angie
 * @description: 轮询
 * @author: sunxiaobo
 * @create: 2020-05-19 17:04
 **/
public class PollingRouter implements Router {
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
        lock.lock();
        if(index>=servers.size()){
            index = 0;
        }
        URL server = servers.get(index++);
        lock.unlock();
        if(server==null){
            server = servers.get(0);
        }
        return server;
    }
}
