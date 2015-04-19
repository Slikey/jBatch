package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 16.04.2015
 */
public class Packet6KeepAlive extends Packet {

    public Packet6KeepAlive() {

    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {

    }

    @Override
    public Packet read(ByteBuf buf) throws IOException {
        return this;
    }

    @Override
    public void handle(PacketHandler packetListener) {
        packetListener.handle(this);
    }

    @Override
    public String toString() {
        return "Packet6KeepAlive{}";
    }

}
