package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Kevin Carstens
 * @since 23.04.2015
 */
public class PacketJobExecute extends Packet {

    private UUID uuid;
    private String command;

    public PacketJobExecute() {

    }

    public PacketJobExecute(UUID uuid, String command) {
        this.uuid = uuid;
        this.command = command;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        Packet.writeUUID(buf, uuid);
        Packet.writeString(buf, command);
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
        uuid = Packet.readUUID(buf);
        command = Packet.readString(buf);
    }

    @Override
    public String toString() {
        return "JobExecutePacket{" +
                "uuid=" + uuid +
                ", command='" + command + '\'' +
                '}';
    }

}
