package com.chopin.sunny.annotion;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface AngieReference {
    String id() default "";
    String app() default "angie-default-app";
    String path() default "";
    String router() default "random_with_weight";
    String group() default "default-0";
    long timeout();


}
