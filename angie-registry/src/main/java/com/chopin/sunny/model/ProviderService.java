package com.chopin.sunny.model;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

 /**
  * @description TODO
  * @author chopin.sun 
  * @updateTime 2019/4/4 15:27 
  * @throws 
  */
@Data
@Builder
public class ProviderService {
    private Class<?> serviceItf;
    private transient Object serviceObject;
    private transient Method serviceMethod;
    private String domain;
    private String serverIp;
    private int serverPort;
    private long timeout;
    //该服务提供者权重
    private int weight;
    //服务端线程数
    private int workerThreads;
    //服务提供者唯一标识
    private String appKey;
    //接口别名
    private String alias;
    //服务分组组名
    private String groupName;
}
