package com.chopin.sunny.model;

import lombok.Data;

@Data
public class RpcRequest {

    private String uniqueKey;
    //服务提供者信息
    private URL provider;
    //调用的方法名称
    private String invokedMethodName;
    //传递参数
    private Object[] args;
    //消费端应用名
    private String appName;
    //消费请求超时时长
    private long invokeTimeout;
}
