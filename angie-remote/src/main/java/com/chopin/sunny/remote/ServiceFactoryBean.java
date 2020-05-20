package com.chopin.sunny.remote;

import com.chopin.sunny.model.ServiceItf;
import com.chopin.sunny.model.URL;
import com.chopin.sunny.registry.RegistryFactory;
import com.chopin.sunny.registry.api.Registry;
import com.chopin.sunny.remote.bootstrap.InterfaceProxy;
import com.chopin.sunny.remote.bootstrap.MethodInterceptorImpl;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-19 21:17
 **/
@Component
public class ServiceFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private Class<?> interfaceClass;
    private T serviceObject;
    private String protocol;
    private String host;
    private int port;
    private int weight;
    private String appKey;
    private String groupName;
    private String className;
    private Method[] methods;

    @Override
    public T getObject() throws Exception {
        if (interfaceClass.isInterface()) {
            serviceObject = (T) InterfaceProxy.newInstance(interfaceClass);
        } else {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(interfaceClass);
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setCallback(new MethodInterceptorImpl());
            serviceObject = (T) enhancer.create();
        }
        return serviceObject;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<URL> providerServiceList = buildServices();
        Registry registry = RegistryFactory.getRegistry();
        providerServiceList.stream().forEach(x->registry.register(x));
    }

    private List<URL> buildServices(){
        List<URL> urls = Arrays.asList(methods).stream().map(x->URL.builder()
                .appKey(appKey)
                .host(host)
                .port(port)
                .groupName(groupName)
                .weight(weight)
                .service(ServiceItf.builder()
                        .className(className)
                        .methodName(x.getName())
                        .build())
                .build()).collect(Collectors.toList());

        return urls;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethods(Method[] methods) {
        this.methods = methods;
    }

}
