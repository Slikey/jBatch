package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.UUID;

/**
 * @author Kevin
 * @since 12.09.2015
 */
public class PacketJobStart extends Packet {

    private UUID uuid;
    private String command;

    public PacketJobStart() {
        super();
    }

    public PacketJobStart(UUID uuid, String command) {
        this();
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
    public void read(ByteBuf buf) throws IOException {
        uuid = readUUID(buf);
        command = readString(buf);
    }

    @Override
    public String toString() {
        return "PacketJobStart{" +
                "uuid=" + uuid +
                ", command='" + command + '\'' +
                '}';
    }

}
