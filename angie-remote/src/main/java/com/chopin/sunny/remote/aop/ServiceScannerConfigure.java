package com.chopin.sunny.remote.aop;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-20 17:12
 **/
@Component
public class ServiceScannerConfigure implements BeanFactoryPostProcessor, ApplicationContextAware {
    private ApplicationContext applicationContext;
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        ServiceAnnotationScanner scanner = new ServiceAnnotationScanner((BeanDefinitionRegistry)beanFactory);
        scanner.setResourceLoader(this.applicationContext);
        scanner.scan("*");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
