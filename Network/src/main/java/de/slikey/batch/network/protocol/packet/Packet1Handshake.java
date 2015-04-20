package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin
 * @since 24.03.2015
 */
public class Packet1Handshake extends Packet {

    private int version;

    public Packet1Handshake() {
    }

    public Packet1Handshake(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        buf.writeInt(version);
    }

    @Override
    public Packet1Handshake read(ByteBuf buf) throws IOException {
        version = buf.readInt();
        return this;
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    @Override
    public String toString() {
        return "Packet1Handshake {"
                + "version=" + version
                + "}";
    }
}
