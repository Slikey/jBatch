package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Kevin Carstens
 * @since 23.04.2015
 */
public class JobResponsePacket extends Packet {

    private UUID uuid;
    private int returnCode;

    public JobResponsePacket() {

    }

    public JobResponsePacket(UUID uuid, int returnCode) {
        this.uuid = uuid;
        this.returnCode = returnCode;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        writeUUID(buf, uuid);
        buf.writeInt(returnCode);
    }

    @Override
    public Packet read(ByteBuf buf) throws IOException {
        uuid = readUUID(buf);
        returnCode = buf.readInt();
        return this;
    }

    @Override
    public void handle(PacketHandler packetListener) {
        packetListener.handle(this);
    }

    @Override
    public String toString() {
        return "JobResponsePacket{" +
                "uuid=" + uuid +
                ", returnCode=" + returnCode +
                '}';
    }
}
