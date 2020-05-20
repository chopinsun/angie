package com.chopin.sunny.utils;


import com.chopin.sunny.enums.SerializeType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

public class PropertyConfigeHelper {

    private final static Map<String,Object> propertyMap = new HashMap<>();


    public static void setProperty(String key,Object value){
        propertyMap.putIfAbsent(key, value);
    }

   public void init(){

   }

    /**
     * 获取properties值
     * @return
     */
    public static Object getProperty(String propertyKey){
        if("SerializerType".equals(propertyKey)){
            return getSerializerType();
        }
        return propertyMap.get(propertyKey);
    }

    public static SerializeType getSerializerType(){
        return SerializeType.queryByType("DefaultJavaSerializer");
    }

    public static SerializeType getSerializerType(String type){
        return SerializeType.queryByType(type);
    }


}
