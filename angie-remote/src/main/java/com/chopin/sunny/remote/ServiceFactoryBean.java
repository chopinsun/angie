package com.chopin.sunny.remote;

import com.chopin.sunny.model.ServiceItf;
import com.chopin.sunny.model.URL;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-19 21:17
 **/
@Component
public class ServiceFactoryBean implements FactoryBean, InitializingBean {

    private String protocol;
    private String host;
    private int port;
    private int weight;
    private String appKey;
    private String groupName;
    private String className;
    private String methodName;

    @Override
    public Object getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}
