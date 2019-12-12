package com.chopin.sunny.serializer;

import com.chopin.sunny.serializer.enums.SerializeType;

public class PropertyConfigeHelper {

    public static SerializeType getSerializerType(){

        return SerializeType.queryByType("DefaultJavaSerializer");
    }

    public static SerializeType getSerializerType(String type){

        return SerializeType.queryByType(type);
    }
}
