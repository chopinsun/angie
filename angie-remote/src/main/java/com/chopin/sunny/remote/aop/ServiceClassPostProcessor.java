package com.chopin.sunny.remote.aop;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-20 18:04
 **/
public class ServiceClassPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Method[] methods = bean.getClass().getDeclaredMethods();
        if(methods!=null){

        }


        return bean;
    }
}
