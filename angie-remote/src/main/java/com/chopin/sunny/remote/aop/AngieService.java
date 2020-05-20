package com.chopin.sunny.remote.aop;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AngieService {
    Class<?> interfaceClass() default void.class;
    String interfaceName() default "";
    String app() default "angie-default-app";
    String weight() default "1";
    String alias() ;
    String group() default "default-0";
    Method[] methods() default {};
}
