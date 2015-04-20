package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 20.04.2015
 */
public class Packet3AgentInformation extends Packet {

    private String name;

    public Packet3AgentInformation() {

    }

    public Packet3AgentInformation(String name) {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        writeString(buf, name);
    }

    @Override
    public Packet read(ByteBuf buf) throws IOException {
        readString(buf);
        return this;
    }

    @Override
    public void handle(PacketHandler packetListener) {
        packetListener.handle(this);
    }
}
