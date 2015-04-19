package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class Packet40AgentInformation extends Packet {

    @Override
    public int getId() {
        return 40;
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

    }

}
