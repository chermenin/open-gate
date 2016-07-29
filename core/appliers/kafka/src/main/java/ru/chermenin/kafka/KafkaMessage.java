package ru.chermenin.kafka;

import java.io.*;

abstract public class KafkaMessage implements Serializable {

    public byte[] serialize() {
        try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
            try(ObjectOutputStream o = new ObjectOutputStream(b)){
                o.writeObject(this);
            }
            return b.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static KafkaMessage deserialize(byte[] bytes) {
        try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
            try(ObjectInputStream o = new ObjectInputStream(b)){
                return (KafkaMessage) o.readObject();
            } catch (ClassNotFoundException e) {
                return null;
            }
        } catch (IOException e) {
            return null;
        }
    }
}
