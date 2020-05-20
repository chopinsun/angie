package com.chopin.sunny.serializer;

import com.chopin.sunny.serializer.api.ISerializer;
import com.google.protobuf.GeneratedMessageV3;
import org.apache.commons.lang3.reflect.MethodUtils;

public class ProtoBufSerializer implements ISerializer {
    @Override
    public <T> byte[] serialize(T obj) {
        try{
            if(! (obj instanceof GeneratedMessageV3 )){
                throw  new RuntimeException("not support object type");
            }
            return (byte[]) MethodUtils.invokeMethod(obj,"toByteArray");

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try{
           if(!GeneratedMessageV3.class.isAssignableFrom(clazz)){
               throw  new RuntimeException("not support object type");
           }
           Object o = MethodUtils.invokeStaticMethod(clazz,"getDefaultInstance");
           return (T) MethodUtils.invokeMethod( o,"parseFrom", new Object[] {data});

        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
