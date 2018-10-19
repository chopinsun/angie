package com.chopin.sunny.serializer.enums;

import org.apache.commons.lang3.StringUtils;

public enum SerializerType {
    DefaultJavaSerializer("DefaultJavaSerializer"),
    HessianSerializer("HessianSerializer"),
    JsonSerializer("JsonSerializer"),
    MarshallingSerializer("MarshallingSerializer"),
    ProtoBufSerializer("ProtoBufSerializer"),
    ProtoStuffSerializer("ProtoStuffSerializer"),
    ThriftSerializer("ThriftSerializer");

    private String serializerType;

    private SerializerType(String serializerType){
        this.serializerType = serializerType;
    }

    public static SerializerType queryByType(String serializerType){
        if(StringUtils.isBlank(serializerType)){
            return  null;
        }
        for(SerializerType serializer : SerializerType.values()){
            if(serializer.serializerType.equals(serializerType)){
                return serializer;
            }
        }
        return  null;
    }

}
