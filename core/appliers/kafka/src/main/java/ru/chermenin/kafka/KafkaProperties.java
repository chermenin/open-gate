package ru.chermenin.kafka;

public interface KafkaProperties {

    String TOPIC = "topic";
    String BOOTSTRAP_SERVERS = "bootstrap.servers";
    String ACKS = "acks";
    String RETRIES = "retries";
    String BATCH_SIZE = "batch.size";
    String LINGER_MS = "linger.ms";
    String BUFFER_MEMORY = "buffer.memory";
    String KEY_SERIALIZER = "key.serializer";
    String VALUE_SERIALIZER = "value.serializer";
}
