package ru.chermenin.tungsten.processing;

import com.continuent.tungsten.replicator.applier.RawApplier;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer09;
import org.apache.flink.streaming.util.serialization.DeserializationSchema;
import ru.chermenin.kafka.*;
import ru.chermenin.tungsten.processing.base.StreamProcessor;

import java.io.IOException;
import java.util.Properties;

public abstract class KafkaStreamProcessor extends StreamProcessor<KafkaMessage> {

    protected abstract RawApplier getApplier();

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
        final RawApplier applier = getApplier();
        return new MapFunction<KafkaMessage, KafkaMessage>() {
            public KafkaMessage map(KafkaMessage message) throws Exception {
                if (message instanceof DataMessage) {
                    DataMessage dataMessage = (DataMessage) message;
                    applier.apply(dataMessage.getEvent(), dataMessage.getHeader(), false, false);
                } else if (message instanceof CommitMessage) {
                    applier.commit();
                } else if (message instanceof RollbackMessage) {
                    applier.rollback();
                }
                return message;
            }
        };
    }
}
