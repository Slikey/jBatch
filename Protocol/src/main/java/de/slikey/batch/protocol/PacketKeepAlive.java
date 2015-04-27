package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 16.04.2015
 */
public class PacketKeepAlive extends Packet {

    public PacketKeepAlive() {

    }

    @Override
    public void write(ByteBuf buf) throws IOException {

    }

    @Override
    public void read(ByteBuf buf) throws IOException {
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{}";
    }

}
