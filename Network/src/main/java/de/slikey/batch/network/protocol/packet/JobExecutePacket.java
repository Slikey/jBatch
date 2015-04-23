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
public class JobExecutePacket extends Packet {

    private UUID uuid;
    private String command;

    public JobExecutePacket() {

    }

    public JobExecutePacket(UUID uuid, String command) {
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
        writeUUID(buf, uuid);
        writeString(buf, command);
    }

    @Override
    public Packet read(ByteBuf buf) throws IOException {
        uuid = readUUID(buf);
        command = readString(buf);
        return this;
    }

    @Override
    public void handle(PacketHandler packetListener) {
        packetListener.handle(this);
    }

    @Override
    public String toString() {
        return "JobExecutePacket{" +
                "uuid=" + uuid +
                ", command='" + command + '\'' +
                '}';
    }

}
