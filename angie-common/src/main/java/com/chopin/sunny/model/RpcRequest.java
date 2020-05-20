package com.chopin.sunny.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {

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
