package ru.chermenin.tungsten.processing;

import com.continuent.tungsten.replicator.applier.RawApplier;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import ru.chermenin.kafka.CommitMessage;
import ru.chermenin.kafka.DataMessage;
import ru.chermenin.kafka.KafkaMessage;
import ru.chermenin.kafka.RollbackMessage;

abstract public class FlinkStreamProcessor implements Processor {

    abstract RawApplier getApplier();

    abstract SourceFunction<KafkaMessage> getSource();

    abstract SinkFunction<KafkaMessage> getSink();

    public void execute() {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        final RawApplier applier = getApplier();
        environment
                .addSource(getSource())
                .map(new MapFunction<KafkaMessage, KafkaMessage>() {
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
                })
                .addSink(getSink());
    }
}
