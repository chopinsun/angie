package com.chopin.sunny.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-13 12:00
 **/
@Builder
@Data
@AllArgsConstructor
public class ServiceItf implements Serializable {
    //接口名称
    private final String name;
    //类名
    private final String className;

    //接口别名
    private final String alias;
    //方法名称
    private final String methodName;
    //方法对应参数
    private final List<Object> methodParams;

    //状态
    private final String stauts;

    //缓存
    private transient Object serviceObject;

    private transient List<URL> listeners;

    public String toServicePath(){
        return "/" + name +"-"+ className +"-"+ alias;
    }

    public static ServiceItf valueOf(String url){
        if(StringUtils.isBlank(url)){
            throw new IllegalArgumentException("url is null");
        }
        url = url.substring(url.indexOf("/"));
        String[] str = url.split("-");
        if(str.length== 3){
            return ServiceItf.builder().name(str[0]).className(str[1]).alias(str[2]).build();
        }
        return null;
    }

    public static ServiceItf valueOf(byte[] data){
        return JSONObject.parseObject(data,ServiceItf.class);
    }

    public void addListener(URL url){
        if(listeners==null){
            synchronized (listeners){
                if(listeners == null){
                    listeners = new ArrayList<>();
                }
            }
        }
        listeners.add(url);
    }

}
