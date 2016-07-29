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
import ru.chermenin.kafka.CommitMessage;
import ru.chermenin.kafka.DataMessage;
import ru.chermenin.kafka.KafkaMessage;
import ru.chermenin.kafka.RollbackMessage;

import java.util.Properties;


public class KafkaApplier implements RawApplier {
    private static Logger logger = Logger.getLogger(KafkaApplier.class);

    private int taskId;
    private ReplDBMSHeader lastHeader;
    private Producer<String, KafkaMessage> producer;

    public void setTaskId(int id) {
        taskId = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void apply(DBMSEvent event, ReplDBMSHeader header, boolean b, boolean b1) throws ReplicatorException, InterruptedException {
        producer.send(new ProducerRecord<String, KafkaMessage>("test", header.getEventId(), new DataMessage(header, event)));
        lastHeader = header;
        logger.info("Apply event: " + header.getEventId());
    }

    public void commit() throws ReplicatorException, InterruptedException {
        producer.send(new ProducerRecord<String, KafkaMessage>("test", "commit" + System.nanoTime(), new CommitMessage()));
        logger.info("Commit event");
    }

    public void rollback() throws InterruptedException {
        producer.send(new ProducerRecord<String, KafkaMessage>("test", "rollback" + System.nanoTime(), new RollbackMessage()));
        logger.info("Rollback event");
    }

    public ReplDBMSHeader getLastEvent() throws ReplicatorException, InterruptedException {
        return lastHeader;
    }

    public void configure(PluginContext pluginContext) throws ReplicatorException, InterruptedException {

    }

    public void prepare(PluginContext pluginContext) throws ReplicatorException, InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "ru.chermenin.tungsten.serialization.KafkaSerializer");
        props.put("value.serializer", "ru.chermenin.tungsten.serialization.KafkaSerializer");
        producer = new KafkaProducer<>(props);
    }

    public void release(PluginContext pluginContext) throws ReplicatorException, InterruptedException {
        if (producer != null) {
            producer.close();
            producer = null;
        }
    }
}
