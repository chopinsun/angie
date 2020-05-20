package com.chopin.sunny.cluster.router;

import com.chopin.sunny.model.URL;

import java.util.List;

/**
 * @title: angie
 * @description: 软路由
 * @author: sunxiaobo
 * @create: 2020-05-19 16:53
 **/
public interface Router {

     URL one(List<URL> servers);

}
