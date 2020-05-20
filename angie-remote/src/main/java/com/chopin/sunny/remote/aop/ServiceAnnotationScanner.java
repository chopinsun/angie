package com.chopin.sunny.remote.aop;

import com.chopin.sunny.remote.ServiceFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Set;

/**
 * @title: angie
 * @description:
 * @author: sunxiaobo
 * @create: 2020-05-20 17:10
 **/
public class ServiceAnnotationScanner extends ClassPathBeanDefinitionScanner {
    public ServiceAnnotationScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }
    public void registerDefaultFilters() {
        this.addIncludeFilter(new AnnotationTypeFilter(AngieService.class));
    }

    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            definition.getPropertyValues()
                    .add("className", definition.getBeanClassName());
            definition.setBeanClass(ServiceFactoryBean.class);
        }
        return beanDefinitions;
    }

    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return super.isCandidateComponent(beanDefinition) && beanDefinition.getMetadata()
                .hasAnnotation(AngieService.class.getName());
    }

}
