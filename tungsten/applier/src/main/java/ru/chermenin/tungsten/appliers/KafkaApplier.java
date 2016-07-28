package ru.chermenin.tungsten.appliers;

import com.continuent.tungsten.replicator.ReplicatorException;
import com.continuent.tungsten.replicator.applier.RawApplier;
import com.continuent.tungsten.replicator.event.DBMSEvent;
import com.continuent.tungsten.replicator.event.ReplDBMSHeader;
import com.continuent.tungsten.replicator.plugin.PluginContext;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.Logger;

import java.util.Properties;


public class KafkaApplier implements RawApplier {
    private static Logger logger = Logger.getLogger(KafkaApplier.class);

    private int taskId;
    private ReplDBMSHeader lastHeader;
    private final Producer<ReplDBMSHeader, DBMSEvent> producer;

    public KafkaApplier() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "ru.chermenin.tungsten.serialization.UniversalSerializer");
        props.put("value.serializer", "ru.chermenin.tungsten.serialization.UniversalSerializer");
        producer = new KafkaProducer<>(props);
    }

    public void setTaskId(int id) {
        taskId = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void apply(DBMSEvent event, ReplDBMSHeader header, boolean b, boolean b1) throws ReplicatorException, InterruptedException {
        producer.send(new ProducerRecord<>("test", header, event));
        lastHeader = header;
        logger.info("Event: " + header.getEventId());
    }

    public void commit() throws ReplicatorException, InterruptedException {

    }

    public void rollback() throws InterruptedException {

    }

    public ReplDBMSHeader getLastEvent() throws ReplicatorException, InterruptedException {
        return lastHeader;
    }

    public void configure(PluginContext pluginContext) throws ReplicatorException, InterruptedException {

    }

    public void prepare(PluginContext pluginContext) throws ReplicatorException, InterruptedException {

    }

    public void release(PluginContext pluginContext) throws ReplicatorException, InterruptedException {

    }
}
