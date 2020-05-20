package com.chopin.sunny.cluster.router.impl;

import com.chopin.sunny.cluster.router.Router;
import com.chopin.sunny.model.URL;

import java.util.List;
import java.util.Random;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-19 17:07
 **/
public class HashRouter implements Router {
    @Override
    public URL one(List<URL> servers) {
        int n = new Random().nextInt(servers.size());
        return servers.get(n);
    }
}
