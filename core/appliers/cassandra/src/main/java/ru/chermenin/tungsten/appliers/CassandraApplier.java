package ru.chermenin.tungsten.appliers;

import com.continuent.tungsten.replicator.ReplicatorException;
import com.continuent.tungsten.replicator.applier.RawApplier;
import com.continuent.tungsten.replicator.event.DBMSEvent;
import com.continuent.tungsten.replicator.event.ReplDBMSHeader;
import com.continuent.tungsten.replicator.plugin.PluginContext;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.log4j.Logger;

import java.io.Closeable;
import java.io.IOException;

public class CassandraApplier implements RawApplier {
    private static Logger logger = Logger.getLogger(CassandraApplier.class);

    private int taskId;

    /**
     * Cassandra cluster connector.
     */
    private Cluster cluster;

    /**
     * Cassandra connection session.
     */
    private Session session;

    /**
     * Cassandra cluster contact point.
     */
    private String clusterContactPoint;

    private ReplDBMSHeader lastHeader;

    @Override
    public void setTaskId(int id) {
        taskId = id;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setClusterContactPoint(String clusterContactPoint) {
        this.clusterContactPoint = clusterContactPoint;
    }

    @Override
    public void apply(DBMSEvent event, ReplDBMSHeader header, boolean b, boolean b1) throws ReplicatorException, InterruptedException {
        // TODO: apply event
        lastHeader = header;
    }

    @Override
    public void commit() throws ReplicatorException, InterruptedException {

    }

    @Override
    public void rollback() throws InterruptedException {

    }

    @Override
    public ReplDBMSHeader getLastEvent() throws ReplicatorException, InterruptedException {
        return lastHeader;
    }

    @Override
    public void configure(PluginContext pluginContext) throws ReplicatorException, InterruptedException {

    }

    @Override
    public void prepare(PluginContext pluginContext) throws ReplicatorException, InterruptedException {
        cluster = Cluster.builder().addContactPoint(clusterContactPoint).build();
        session = cluster.connect();
    }

    @Override
    public void release(PluginContext pluginContext) throws ReplicatorException, InterruptedException {
        session = close(session);
        cluster = close(cluster);
    }

    /**
     * Close closeable =)
     */
    private <T extends Closeable> T close(T c) throws ReplicatorException {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                throw new ReplicatorException(e);
            }
        }
        return null;
    }
}