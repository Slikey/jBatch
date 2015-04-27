package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 20.04.2015
 */
public class PacketAgentInformation extends Packet {

    public static final String PASSWORD = "hardCoreSavePasswordYo!";

    private String name;
    private String password;

    public PacketAgentInformation() {

    }

    public PacketAgentInformation(String name, String password) {
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
    public void write(ByteBuf buf) throws IOException {
        Packet.writeString(buf, name);
        Packet.writeString(buf, password);
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
        name = Packet.readString(buf);
        password = Packet.readString(buf);
    }

    @Override
    public String toString() {
        return "AgentInformationPacket{" +
                "name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PacketAgentInformation)) return false;

        PacketAgentInformation that = (PacketAgentInformation) o;

        if (!name.equals(that.name)) return false;
        if (!password.equals(that.password)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + password.hashCode();
        return result;
    }
}
