package ru.chermenin.tungsten.processing.base;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

abstract public class StreamProcessor<T> implements Processor {

    abstract protected SourceFunction<T> getSource();

    abstract protected SinkFunction<T> getSink();

    abstract protected MapFunction<T, T> getMap();

    public void execute() throws Exception {
        StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment
                .addSource(getSource())
                .map(getMap())
                .addSink(getSink());
        environment.execute();
    }
}
