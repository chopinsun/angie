package com.chopin.sunny.serializer.api;

public interface ISerializer {

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    <T> T deserialize(byte[] data, Class<T> clazz);
}
