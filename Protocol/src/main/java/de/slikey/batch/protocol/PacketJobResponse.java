package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Kevin Carstens
 * @since 23.04.2015
 */
public class PacketJobResponse extends Packet {

    private UUID uuid;
    private int returnCode;

    public PacketJobResponse() {

    }

    public PacketJobResponse(UUID uuid, int returnCode) {
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
        Packet.writeUUID(buf, uuid);
        buf.writeInt(returnCode);
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
        uuid = Packet.readUUID(buf);
        returnCode = buf.readInt();
    }

    @Override
    public String toString() {
        return "JobResponsePacket{" +
                "uuid=" + uuid +
                ", returnCode=" + returnCode +
                '}';
    }
}
