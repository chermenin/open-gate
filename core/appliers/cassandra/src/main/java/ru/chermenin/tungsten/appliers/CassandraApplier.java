package ru.chermenin.tungsten.appliers;

import com.continuent.tungsten.replicator.ReplicatorException;
import com.continuent.tungsten.replicator.applier.RawApplier;
import com.continuent.tungsten.replicator.consistency.ConsistencyException;
import com.continuent.tungsten.replicator.event.DBMSEvent;
import com.continuent.tungsten.replicator.event.ReplDBMSHeader;
import com.continuent.tungsten.replicator.plugin.PluginContext;

public class CassandraApplier implements RawApplier {

    @Override
    public void setTaskId(int i) {

    }

    @Override
    public void apply(DBMSEvent dbmsEvent, ReplDBMSHeader replDBMSHeader, boolean b, boolean b1) throws ReplicatorException, ConsistencyException, InterruptedException {

    }

    @Override
    public void commit() throws ReplicatorException, InterruptedException {

    }

    @Override
    public void rollback() throws InterruptedException {

    }

    @Override
    public ReplDBMSHeader getLastEvent() throws ReplicatorException, InterruptedException {
        return null;
    }

    @Override
    public void configure(PluginContext pluginContext) throws ReplicatorException, InterruptedException {

    }

    @Override
    public void prepare(PluginContext pluginContext) throws ReplicatorException, InterruptedException {

    }

    @Override
    public void release(PluginContext pluginContext) throws ReplicatorException, InterruptedException {

    }
}