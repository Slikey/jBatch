package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.BufferWrapper;
import de.slikey.batch.network.protocol.Packet;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 16.04.2015
 */
public class PacketKeepAlive extends Packet {

    public PacketKeepAlive() {
        super();
    }

    @Override
    public void write(BufferWrapper buf) throws IOException {

    }

    @Override
    public void read(BufferWrapper buf) throws IOException {

    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{}";
    }

}
