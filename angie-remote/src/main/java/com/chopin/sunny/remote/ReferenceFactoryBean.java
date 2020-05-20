package com.chopin.sunny.remote;

import com.chopin.sunny.model.ServiceItf;
import com.chopin.sunny.model.URL;
import com.chopin.sunny.registry.RegistryFactory;
import com.chopin.sunny.registry.api.Registry;
import com.chopin.sunny.remote.netty.NettyChannelPoolFactroy;
import com.chopin.sunny.remote.netty.NettyInvokerProxy;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-19 21:17
 **/
@Log4j2
@Component
public class ReferenceFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private String clientAppKey;
    private String clientHost;
    private String clientPort;
    private String className;
    private String methodName;
    private int timeout;
    private T serviceObject;
    private String clusterStrategy;
    private String remoteAppKey;
    private String group;
    private String alias;

    @Override
    public T getObject() throws Exception {
        return serviceObject;
    }

    @Override
    public Class<?> getObjectType() {
        try {
            Class innerClass = Class.forName(className);
            return innerClass;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Registry registry = RegistryFactory.getRegistry();
        Map<URL, Set<URL>> subscribes = registry.getLocalSubscribeCaches();
        ServiceItf service = ServiceItf.builder()
                .className(className)
                .alias(alias)
                .methodName(methodName)
                .build();
        NettyChannelPoolFactroy.getInstance().init();
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(T serviceObject) {
        this.serviceObject = serviceObject;
    }

    public String getClusterStrategy() {
        return clusterStrategy;
    }

    public void setClusterStrategy(String clusterStrategy) {
        this.clusterStrategy = clusterStrategy;
    }

    public String getRemoteAppKey() {
        return remoteAppKey;
    }

    public void setRemoteAppKey(String remoteAppKey) {
        this.remoteAppKey = remoteAppKey;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
