package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.BufferWrapper;
import de.slikey.batch.network.protocol.Packet;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 24.04.2015
 */
public class PacketException extends Packet {

    private String name;
    private Exception exception;

    public PacketException() {
        super();
    }

    public PacketException(Exception exception) {
        this.name = exception.getClass().getName();
        this.exception = exception;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public void write(BufferWrapper buf) throws IOException {
        buf.writeString(name);
        buf.writeException(exception);
    }

    @Override
    public void read(BufferWrapper buf) throws IOException {
        name = buf.readString();
        exception = buf.readException();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", exception=" + exception +
                '}';
    }
}
