package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

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
    public void write(ByteBuf buf) throws IOException {
        writeString(buf, name);
        writeException(buf, exception);
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
        name = readString(buf);
        exception = readException(buf);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", exception=" + exception +
                '}';
    }
}
