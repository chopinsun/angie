package com.chopin.sunny.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
        return "/" + className;
    }

    public static ServiceItf valueOf(String url){
        if(StringUtils.isBlank(url)){
            throw new IllegalArgumentException("url is null");
        }
        url = url.substring(url.indexOf("@")+1);

        return ServiceItf.builder().className(url).build();
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
