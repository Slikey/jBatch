package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 16.04.2015
 */
public class PacketKeepAlive extends Packet {

    public static final PacketKeepAlive instance = new PacketKeepAlive();
    public static int TIMEOUT_SECONDS = 15;

    public PacketKeepAlive() {
        super();
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
