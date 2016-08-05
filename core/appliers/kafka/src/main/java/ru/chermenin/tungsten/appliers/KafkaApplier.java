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
import ru.chermenin.kafka.*;
import ru.chermenin.tungsten.serialization.KafkaSerializer;

import java.util.Properties;

import static ru.chermenin.kafka.KafkaProperties.*;


public class KafkaApplier implements RawApplier {
    private static Logger logger = Logger.getLogger(KafkaApplier.class);

    private int taskId;

    /**
     * Kafka configuration properties.
     */
    private String bootstrapServers;
    private String acks;
    private int retries;
    private int batchSize;
    private int lingerMs;
    private int bufferMemory;

    /**
     * Kafka messages producer.
     */
    private Producer<String, KafkaMessage> producer;

    private ReplDBMSHeader lastHeader;

    public void setTaskId(int id) {
        taskId = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public void setAcks(String acks) {
        this.acks = acks;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setLingerMs(int lingerMs) {
        this.lingerMs = lingerMs;
    }

    public void setBufferMemory(int bufferMemory) {
        this.bufferMemory = bufferMemory;
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
        props.put(BOOTSTRAP_SERVERS, bootstrapServers);
        props.put(ACKS, acks);
        props.put(RETRIES, retries);
        props.put(BATCH_SIZE, batchSize);
        props.put(LINGER_MS, lingerMs);
        props.put(BUFFER_MEMORY, bufferMemory);
        props.put(KEY_SERIALIZER, KafkaSerializer.class.getCanonicalName());
        props.put(VALUE_SERIALIZER, KafkaSerializer.class.getCanonicalName());
        producer = new KafkaProducer<>(props);
    }

    public void release(PluginContext pluginContext) throws ReplicatorException, InterruptedException {
        if (producer != null) {
            producer.close();
            producer = null;
        }
    }
}
