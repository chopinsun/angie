package com.chopin.sunny.serializer;

import com.chopin.sunny.serializer.api.ISerializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProtoStuffSerializer implements ISerializer {
    private static Map<Class<?>,Schema<?>> cs = new ConcurrentHashMap<>();
    private static Objenesis objenesis = new ObjenesisStd(true);

    private static <T> Schema<T> getSchema(Class<T> clazz){
        Schema<T> s =(Schema<T>) cs.get(clazz);
        if( s == null){
            s = RuntimeSchema.createFrom(clazz);
            cs.put(clazz,s);
        }
        return s;
    }
    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> clazz = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema<T> schema = getSchema(clazz);
            return ProtostuffIOUtil.toByteArray(obj,schema,buffer);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        try{
            T message = (T) objenesis.newInstance(clazz);
            Schema<T> schema = getSchema(clazz);
            ProtostuffIOUtil.mergeFrom(data,message,schema);
            return message;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
