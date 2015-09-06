package de.slikey.batch.network.protocol;

import java.io.IOException;

/**
 * @author Kevin
 * @since 23.03.2015
 */
public abstract class Packet implements SerializableObject {

    protected int id;

    public Packet() {
    }

    public abstract void write(BufferWrapper buf) throws IOException;

    public abstract void read(BufferWrapper buf) throws IOException;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public abstract String toString();

}
