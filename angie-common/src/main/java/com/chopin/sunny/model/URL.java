package com.chopin.sunny.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.net.InetSocketAddress;

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
        return service.getClassName()+"."+service.getMethodName();
    }

    public InetSocketAddress toInetSocketAddress(){
        return new InetSocketAddress(host,port);
    }

    public URL copy(){
        return  URL.builder()
                .host(this.host)
                .port(this.port)
                .appKey(this.appKey)
                .groupName(this.groupName)
                .protocol(this.protocol)
                .timeout(this.timeout)
                .weight(this.weight)
                .service(this.service)
                .build();
    }
}
