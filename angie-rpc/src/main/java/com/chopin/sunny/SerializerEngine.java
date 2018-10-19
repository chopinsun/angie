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

}
