package ru.chermenin.tungsten.serialization;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import ru.chermenin.kafka.KafkaMessage;

import java.io.*;
import java.util.Map;

public class KafkaSerializer implements Closeable, Serializer<KafkaMessage>, Deserializer<KafkaMessage> {

    @Override
    public void configure(Map<String, ?> map, boolean b) {

    }

    @Override
    public byte[] serialize(String s, KafkaMessage message) {
        return message.serialize();
    }

    @Override
    public KafkaMessage deserialize(String s, byte[] bytes) {
        return KafkaMessage.deserialize(bytes);
    }

    @Override
    public void close() {

    }
}
