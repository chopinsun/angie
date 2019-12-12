package com.chopin.sunny.serializer.enums;

import org.apache.commons.lang3.StringUtils;

public enum SerializeType {
    DefaultJavaSerializer("DefaultJavaSerializer"),
    HessianSerializer("HessianSerializer"),
    JsonSerializer("JsonSerializer"),
    MarshallingSerializer("MarshallingSerializer"),
    ProtoBufSerializer("ProtoBufSerializer"),
    ProtoStuffSerializer("ProtoStuffSerializer"),
    ThriftSerializer("ThriftSerializer");

    private String serializeType;

    private SerializeType(String serializeType){
        this.serializeType = serializeType;
    }

    public static SerializeType queryByType(String serializeType){
        if(StringUtils.isBlank(serializeType)){
            return  null;
        }
        for(SerializeType serializer : SerializeType.values()){
            if(serializer.serializeType.equals(serializeType)){
                return serializer;
            }
        }
        return  null;
    }

    public String getSerializeType() {
        return serializeType;
    }

}
