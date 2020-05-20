package com.chopin.sunny.remote.aop;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
@Inherited
public @interface Method {
    String name();
    int timeout() default -1;
    Argument[] arguments() default {};

}
