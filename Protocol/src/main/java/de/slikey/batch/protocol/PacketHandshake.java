package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 27.04.2015
 */
public class PacketHandshake extends Packet {

    private int version;

    public PacketHandshake() {

    }

    public PacketHandshake(int version) {
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
    public void read(ByteBuf buf) throws IOException {
        version = buf.readInt();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "version=" + version +
                '}';
    }
}
