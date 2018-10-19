package com.chopin.sunny.serializer;

import com.chopin.sunny.serializer.api.ISerializer;

public class MarshallingSerializer implements ISerializer {
    @Override
    public <T> byte[] serialize(T obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return null;
    }
}
