package com.chopin.sunny.serializer;

import com.alibaba.fastjson.JSONObject;
import com.chopin.sunny.serializer.api.ISerializer;

public class JsonSerializer implements ISerializer {
    @Override
    public <T> byte[] serialize(T obj) {
        return JSONObject.toJSONBytes(obj);
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) {
        return JSONObject.parseObject(data,clazz);
    }
}
