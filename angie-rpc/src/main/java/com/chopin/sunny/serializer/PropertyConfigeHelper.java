package com.chopin.sunny.serializer;

import com.chopin.sunny.serializer.enums.SerializeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PropertyConfigeHelper {

    private final static Map<String,Object> propertyMap = new HashMap<>();


    public static void setProperty(String key,Object value){
        propertyMap.putIfAbsent(key, value);
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
