package com.chopin.sunny;

import com.chopin.sunny.serializer.*;
import com.chopin.sunny.serializer.api.ISerializer;
import com.chopin.sunny.serializer.enums.SerializerType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerEngine {
    private static Map<SerializerType,ISerializer> serializeMap = new ConcurrentHashMap<>();
    static{
        serializeMap.put(SerializerType.DefaultJavaSerializer, new DefaultJavaSerializer());
        serializeMap.put(SerializerType.HessianSerializer, new HessianSerializer());
        serializeMap.put(SerializerType.JsonSerializer, new JsonSerializer());
        serializeMap.put(SerializerType.MarshallingSerializer, new MarshallingSerializer());
        serializeMap.put(SerializerType.ProtoBufSerializer, new ProtoBufSerializer());
        serializeMap.put(SerializerType.ProtoStuffSerializer, new ProtoStuffSerializer());
        serializeMap.put(SerializerType.ThriftSerializer, new ThriftSerializer());
    }

    public <T> byte[] serialize(T obj,String serializeType){
        SerializerType st =  SerializerType.queryByType(serializeType);
        if(st==null){
            throw new RuntimeException("unsupport serializeType");
        }
        ISerializer serializer = serializeMap.get(st);
        if(serializer == null){
            throw new RuntimeException("unsupport serializer");
        }
        try{
            return serializer.serialize(obj);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    /**
     * 反序列化
     * @param data
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> T deserialize(byte[] data, Class<T> clazz,String serializeType){
        SerializerType st =  SerializerType.queryByType(serializeType);
        if(st==null){
            throw new RuntimeException("unsupport serializeType");
        }
        ISerializer serializer = serializeMap.get(serializeType);
        if(serializer == null){
            throw new RuntimeException("unsupport serializer");
        }
        try{
            return serializer.deserialize(data,clazz);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
