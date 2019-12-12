package com.chopin.sunny.model;

import lombok.Data;

@Data
public class RpcResponse {
    //UUID,唯一标识一次返回值
    private String uniqueKey;
    //客户端指定的服务超时时间
    private long invokeTimeout;
    //接口调用返回的结果对象
    private Object result;
}
