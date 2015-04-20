package de.slikey.batch.network.protocol.packet;

import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 20.04.2015
 */
public class Packet40AgentInformation extends Packet {

    public static final String USERNAME = "Swegger123", PASSWORD = "hardCoreSavePasswordYo!";

    private String name;
    private String password;

    public Packet40AgentInformation() {

    }

    public Packet40AgentInformation(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public int getId() {
        return 40;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        writeString(buf, name);
        writeString(buf, password);
    }

    @Override
    public Packet read(ByteBuf buf) throws IOException {
        name = readString(buf);
        password = readString(buf);
        return this;
    }

    @Override
    public void handle(PacketHandler packetListener) {
        packetListener.handle(this);
    }
}
