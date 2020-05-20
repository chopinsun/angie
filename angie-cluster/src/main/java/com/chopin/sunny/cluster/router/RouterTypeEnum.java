package com.chopin.sunny.cluster.router;

/**
 * @title: angie
 * @description: 软路由算法枚举
 * @author: sunxiaobo
 * @create: 2020-05-19 16:57
 **/
public enum  RouterTypeEnum {
    Random("random"),
    RandomWithWeight("random_with_weight"),
    Polling("polling"),
    PollingWithWeight("polling_with_weight"),
    Hash("hash"),
    MinConnect("minconnect"),
    ;

    private String type;

    RouterTypeEnum(String type) {
        this.type = type;
    }

}
