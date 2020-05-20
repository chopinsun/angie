package com.chopin.sunny.remote.bootstrap;

import com.chopin.sunny.model.ServiceItf;
import com.chopin.sunny.model.URL;
import com.chopin.sunny.registry.RegistryFactory;
import com.chopin.sunny.registry.api.Registry;
import com.chopin.sunny.remote.bootstrap.InterfaceProxy;
import com.chopin.sunny.remote.bootstrap.MethodInterceptorImpl;
import com.chopin.sunny.remote.netty.NettyChannelPoolFactroy;
import com.chopin.sunny.remote.netty.NettyInvokerProxy;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-19 21:17
 **/
@Log4j2
@Component
public class ReferenceFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private Class<?> interfaceClass;
    private String clientAppKey;
    private String clientHost;
    private int clientPort;
    private String className;
    private Method[] methods;
    private int timeout;
    private T serviceObject;
    private String clusterStrategy;
    private String remoteAppKey;
    private String group;
    private String alias;

    @Override
    public T getObject() throws Exception {
        interfaceClass = Class.forName(className);
        serviceObject =  (T) NettyInvokerProxy.newInstance(interfaceClass);
        return serviceObject;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Registry registry = RegistryFactory.getRegistry();
        registry.subscribe(buildService(),buildClients());
        NettyChannelPoolFactroy.getInstance().init();
    }


    private Set<URL> buildClients(){
        Set<URL> urls = Arrays.asList(methods).stream().map(x->URL.builder()
                .appKey(clientAppKey)
                .host(clientHost)
                .port(clientPort)
                .groupName(group)
                .service(ServiceItf.builder()
                        .className(className)
                        .methodName(x.getName())
                        .build())
                .build()).collect(Collectors.toSet());

        return urls;
    }

    private URL buildService(){
        return URL.builder()
                .appKey(remoteAppKey)
                .service(ServiceItf.builder().className(className).build())
                .build();
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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
