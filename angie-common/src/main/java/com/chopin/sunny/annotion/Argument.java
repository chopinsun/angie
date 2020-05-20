package com.chopin.sunny.annotion;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
@Inherited
public @interface Argument {
    int index() default -1;
    String type() default "";
}
