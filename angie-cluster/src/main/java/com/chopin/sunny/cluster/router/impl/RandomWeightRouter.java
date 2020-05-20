package com.chopin.sunny.cluster.router.impl;

import com.chopin.sunny.cluster.router.Router;
import com.chopin.sunny.model.URL;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @title: angie
 * @description: 随机加权
 * @author: sunxiaobo
 * @create: 2020-05-19 17:03
 **/
public class RandomWeightRouter implements Router {
    @Override
    public URL one(List<URL> servers) {
        List<URL> list = addWeight(servers);
        int n = RandomUtils.nextInt(0,list.size() - 1);
        return list.get(n);
    }

    private List<URL> addWeight(List<URL> servers){
        return servers.stream().map(x-> IntStream.range(0,servers.size()).mapToObj(i->x.copy())).flatMap(x->x).collect(Collectors.toList());
    }
}
