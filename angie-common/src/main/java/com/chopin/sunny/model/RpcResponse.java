package com.chopin.sunny.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RpcResponse {
    //UUID,唯一标识一次返回值
    private String uniqueKey;
    //客户端指定的服务超时时间
    private long invokeTimeout;
    @Builder.Default
    private boolean isSuccess = false;

    //接口调用返回的结果对象
    private Object result;

    private String message;

    private Throwable error;

    public static RpcResponseBuilder success(String uniqueKey){
        return RpcResponse.builder()
                .uniqueKey(uniqueKey)
                .isSuccess(true);
    }

    public static RpcResponseBuilder fail(String uniqueKey){
        return RpcResponse.builder()
                .uniqueKey(uniqueKey)
                .isSuccess(false);
    }
}
