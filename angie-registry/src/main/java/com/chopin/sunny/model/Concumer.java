package com.chopin.sunny.model;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author sunxiaobo
 * @title: Concumer
 * @projectName angie
 * @description: TODO
 * @date 2019/4/415:51
 */
@Data
@Builder
public class Concumer {
    private Class<?> serviceItf;
    private transient Object serviceObject;
    private transient Method serviceMethod;
    private String serverIp;
    private int serverPort;
    //服务提供者唯一标识
    private String appKey;
    //服务分组组名
    private String groupName;
}
