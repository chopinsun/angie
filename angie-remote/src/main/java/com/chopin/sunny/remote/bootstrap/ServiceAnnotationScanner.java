package com.chopin.sunny.remote.bootstrap;

import com.chopin.sunny.annotion.AngieService;
import com.chopin.sunny.remote.ServiceFactoryBean;
import com.chopin.sunny.utils.NativeUtil;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.reflect.Method;
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

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        for (BeanDefinitionHolder holder : beanDefinitions) {
            GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
            definition.getPropertyValues()
                    .add("className", definition.getBeanClassName());
            definition.getPropertyValues().add("interfaceClass",definition.getBeanClass());
            try {
                Class<?> clazz = Class.forName(definition.getBeanClassName());
                Method[] methods = clazz.getMethods();
                definition.getPropertyValues().add("methods",methods);
                AngieService service = AnnotationUtils.findAnnotation(clazz, AngieService.class);
                definition.getPropertyValues().add("appKey",service.app());
                definition.getPropertyValues().add("alias",service.alias());
                definition.getPropertyValues().add("group",service.group());
                definition.getPropertyValues().add("weight",service.weight());
                definition.getPropertyValues().add("host", NativeUtil.getLocalIp());
                definition.getPropertyValues().add("port",NativeUtil.getPort());
            } catch (Exception e) {
                e.printStackTrace();
            }
            definition.setBeanClass(ServiceFactoryBean.class);
        }
        return beanDefinitions;
    }

    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return super.isCandidateComponent(beanDefinition) && beanDefinition.getMetadata()
                .hasAnnotation(AngieService.class.getName());
    }

}
