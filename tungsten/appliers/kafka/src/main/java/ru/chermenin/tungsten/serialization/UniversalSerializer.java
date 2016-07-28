package ru.chermenin.tungsten.serialization;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.io.*;
import java.util.Map;

public class UniversalSerializer implements Closeable, Serializer<Object>, Deserializer<Object> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, Object t) {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(t);
            }
            return b.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Object deserialize(String s, byte[] bytes) {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return o.readObject();
            } catch (ClassNotFoundException e) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() {

    }
}
