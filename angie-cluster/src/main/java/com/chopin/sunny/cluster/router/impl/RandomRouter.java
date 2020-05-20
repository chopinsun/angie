package com.chopin.sunny.cluster.router.impl;

import com.chopin.sunny.cluster.router.Router;
import com.chopin.sunny.model.URL;
import org.apache.commons.lang3.RandomUtils;

import java.util.List;
import java.util.Random;

/**
 * @title: angie
 * @description: 随机路由
 * @author: sunxiaobo
 * @create: 2020-05-19 17:03
 **/
public class RandomRouter implements Router {


    @Override
    public URL one(List<URL> servers) {
        int n = RandomUtils.nextInt(0,servers.size() - 1);
        return servers.get(n);
    }
}
