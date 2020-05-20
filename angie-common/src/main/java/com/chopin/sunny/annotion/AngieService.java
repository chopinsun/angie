package com.chopin.sunny.annotion;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AngieService {
    String app() default "angie-default-app";
    String weight() default "1";
    String alias() ;
    String group() default "default-0";
}
