package com.chopin.sunny.remote.netty;

import com.chopin.sunny.model.RpcResponse;
import com.google.common.collect.Maps;
import io.protostuff.Rpc;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-18 22:39
 **/
public class NettyClientResponseCache {

    private static final Map<String, BlockingQueue<RpcResponse>> responseCache = Maps.newConcurrentMap();

    public static void initNewCache(String uniqueKey){
        responseCache.putIfAbsent(uniqueKey,new ArrayBlockingQueue<>(1));
    }

    public static void putResponse(RpcResponse response){
        if(responseCache.get(response.getUniqueKey())!=null){
            responseCache.get(response.getUniqueKey()).add(response);
        }
    }

    public static RpcResponse getResponse(String uniqueKey,long timeout){
        BlockingQueue<RpcResponse> queue = responseCache.get(uniqueKey);
        try {
            RpcResponse response = queue.poll(timeout, TimeUnit.MICROSECONDS);
            responseCache.remove(uniqueKey);
            return response;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


}
