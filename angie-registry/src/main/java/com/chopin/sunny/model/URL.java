package com.chopin.sunny.model;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.ChildData;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
  * @description 服务实体
  * @author chopin.sun 
  * @updateTime 2019/4/4 15:27 
  * @throws 
  */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class URL implements Serializable {
    private String protocol;
    private String host;
    private int port;
    private long timeout;
    //服务提供者权重
    private int weight;
    //服务唯一标识
    private String appKey;

    //服务分组组名
    private String groupName;
    //接口
    private ServiceItf service;



    public String toNodePath(){
        return "/" + appKey+"-"+ host+"-"+ port + groupName +"@";
    }

    public String toFullPath(){
        return toNodePath() + service.toServicePath();
    }

    public static URL valueOf(String url){
        if(StringUtils.isBlank(url)){
            throw new IllegalArgumentException("url is null");
        }
        if(url.contains("@")){
            url = url.substring(0,url.lastIndexOf("@"));
        }
        url = url.substring(url.indexOf("/"));
        String[] str = url.split("-");
        if(str.length== 3){
            return URL.builder().appKey(str[0]).host(str[1]).port(Integer.valueOf(str[2])).build();
        }
        return null;
    }

    public static URL valueOf(String path,byte[] data){
        URL url = valueOf(path);
        ServiceItf service = ServiceItf.valueOf(data);
        url.setService(service);
        return url;
    }

    public String getUniqueKey(){
        return toFullPath();
    }

}
