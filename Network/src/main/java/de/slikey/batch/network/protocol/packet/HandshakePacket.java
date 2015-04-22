package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin
 * @since 24.03.2015
 */
public class HandshakePacket extends Packet {

    private int version;

    public HandshakePacket() {
    }

    public HandshakePacket(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        buf.writeInt(version);
    }

    @Override
    public HandshakePacket read(ByteBuf buf) throws IOException {
        version = buf.readInt();
        return this;
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        packetHandler.handle(this);
    }

    @Override
    public String toString() {
        return "HandshakePacket{"
                + "version=" + version
                + "}";
    }
}
