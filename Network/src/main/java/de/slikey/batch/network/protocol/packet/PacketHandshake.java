package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.SerializableObject;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 27.04.2015
 */
public class PacketHandshake extends Packet implements SerializableObject {

    private int version;
    private ServerType serverType;

    public PacketHandshake() {
        super();
    }

    public PacketHandshake(int version, ServerType serverType) {
        this();
        this.version = version;
        this.serverType = serverType;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        buf.writeInt(version);
        writeServerType(buf, serverType);
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
        version = buf.readInt();
        serverType = readServerType(buf);
    }

    private static void writeServerType(ByteBuf buf, ServerType code) {
        buf.writeByte(code.ordinal());
    }

    private static ServerType readServerType(ByteBuf buf) {
        return ServerType.values()[buf.readByte()];
    }

    public enum ServerType {

        DEFAULT

    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "version=" + version +
                '}';
    }
}
