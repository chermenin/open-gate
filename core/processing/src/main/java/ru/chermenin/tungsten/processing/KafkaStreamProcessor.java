package ru.chermenin.tungsten.processing;

import com.continuent.tungsten.replicator.ReplicatorException;
import com.continuent.tungsten.replicator.applier.RawApplier;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import ru.chermenin.kafka.*;
import ru.chermenin.tungsten.processing.base.StreamProcessor;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

public abstract class KafkaStreamProcessor extends StreamProcessor<KafkaMessage> {

    protected abstract List<RawApplier> getMapAppliers();

    protected abstract List<RawApplier> getSinkAppliers();

    protected abstract Properties getKafkaProperties();

    @Override
    protected SourceFunction<KafkaMessage> getSource() {
        Properties properties = getKafkaProperties();
        return new FlinkKafkaConsumer09<>(properties.getProperty(KafkaProperties.TOPIC),
                new DeserializationSchema<KafkaMessage>() {

                    @Override
                    public KafkaMessage deserialize(byte[] message) throws IOException {
                        return KafkaMessage.deserialize(message);
                    }

                    @Override
                    public boolean isEndOfStream(KafkaMessage nextElement) {
                        return false;
                    }

                    @Override
                    public TypeInformation<KafkaMessage> getProducedType() {
                        return BasicTypeInfo.of(KafkaMessage.class);
                    }
                }, properties);
    }

    @Override
    protected MapFunction<KafkaMessage, KafkaMessage> getMap() {
        return new MapFunction<KafkaMessage, KafkaMessage>() {

            @Override
            public KafkaMessage map(KafkaMessage message) throws Exception {
                return applyMessage(message, getMapAppliers());
            }
        };
    }

    @Override
    protected SinkFunction<KafkaMessage> getSink() {
        return new SinkFunction<KafkaMessage>() {

            @Override
            public void invoke(KafkaMessage message) throws Exception {
                applyMessage(message, getSinkAppliers());
            }
        };
    }

    private KafkaMessage applyMessage(KafkaMessage message, List<RawApplier> appliers) throws ReplicatorException, InterruptedException {
        if (appliers == null) return message;

        if (message instanceof DataMessage) {
            DataMessage dataMessage = (DataMessage) message;
            for (RawApplier applier : appliers) {
                applier.apply(dataMessage.getEvent(), dataMessage.getHeader(), false, false);
            }
        } else if (message instanceof CommitMessage) {
            for (RawApplier applier : appliers) {
                applier.commit();
            }
        } else if (message instanceof RollbackMessage) {
            for (RawApplier applier : appliers) {
                applier.rollback();
            }
        }
        return message;
    }
}
