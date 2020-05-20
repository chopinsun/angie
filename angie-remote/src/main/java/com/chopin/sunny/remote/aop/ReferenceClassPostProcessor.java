package com.chopin.sunny.remote.aop;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-20 18:04
 **/
public class ReferenceClassPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        return bean;
    }
}
