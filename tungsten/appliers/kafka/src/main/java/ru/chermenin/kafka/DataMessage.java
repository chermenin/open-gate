package ru.chermenin.kafka;

import com.continuent.tungsten.replicator.event.DBMSEvent;
import com.continuent.tungsten.replicator.event.ReplDBMSHeader;

public class DataMessage extends KafkaMessage {

    private ReplDBMSHeader header;
    private DBMSEvent event;

    public DataMessage() {

    }

    public DataMessage(ReplDBMSHeader header, DBMSEvent event) {
        this.header = header;
        this.event = event;
    }

    public ReplDBMSHeader getHeader() {
        return header;
    }

    public void setHeader(ReplDBMSHeader header) {
        this.header = header;
    }

    public DBMSEvent getEvent() {
        return event;
    }

    public void setEvent(DBMSEvent event) {
        this.event = event;
    }
}
