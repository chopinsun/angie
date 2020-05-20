package com.chopin.sunny;

import com.chopin.sunny.serializer.*;
import com.chopin.sunny.serializer.api.ISerializer;
import com.chopin.sunny.serializer.enums.SerializeType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SerializerEngine {
    private static Map<SerializeType,ISerializer> serializeMap = new ConcurrentHashMap<>();
    static{
        serializeMap.put(SerializeType.DefaultJavaSerializer, new DefaultJavaSerializer());
        serializeMap.put(SerializeType.HessianSerializer, new HessianSerializer());
        serializeMap.put(SerializeType.JsonSerializer, new JsonSerializer());
        serializeMap.put(SerializeType.MarshallingSerializer, new MarshallingSerializer());
        serializeMap.put(SerializeType.ProtoBufSerializer, new ProtoBufSerializer());
        serializeMap.put(SerializeType.ProtoStuffSerializer, new ProtoStuffSerializer());
        serializeMap.put(SerializeType.ThriftSerializer, new ThriftSerializer());
    }

    public static <T> byte[] serialize(T obj,String serializeType){
        SerializeType st =  SerializeType.queryByType(serializeType);
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
    public static <T> T deserialize(byte[] data, Class<T> clazz,String serializeType){
        SerializeType st =  SerializeType.queryByType(serializeType);
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
