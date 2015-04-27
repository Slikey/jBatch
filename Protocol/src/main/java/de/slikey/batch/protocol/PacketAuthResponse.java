package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 20.04.2015
 */
public class PacketAuthResponse extends Packet {

    private AuthResponseCode code;
    private String message;

    public PacketAuthResponse() {

    }

    public PacketAuthResponse(AuthResponseCode code, String message) {
        this.code = code;
        this.message = message;
    }

    public AuthResponseCode getCode() {
        return code;
    }

    public void setCode(AuthResponseCode code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        writeAuthResponseCode(buf, code);
        Packet.writeString(buf, message);
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
        code = readAuthResponseCode(buf);
        message = Packet.readString(buf);
    }

    private void writeAuthResponseCode(ByteBuf buf, AuthResponseCode code) {
        buf.writeByte(code.ordinal());
    }

    private AuthResponseCode readAuthResponseCode(ByteBuf buf) {
        return AuthResponseCode.values()[buf.readByte()];
    }

    @Override
    public String toString() {
        return "AuthResponsePacket{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public static enum AuthResponseCode {

        SUCCESS,
        ERROR

    }

}
