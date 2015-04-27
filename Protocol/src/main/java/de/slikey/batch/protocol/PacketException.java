package de.slikey.batch.protocol;

import de.slikey.batch.network.protocol.Packet;
import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin Carstens
 * @since 24.04.2015
 */
public class PacketException extends Packet {

    private String name;
    private Exception exception;

    public PacketException() {

    }

    public PacketException(Exception exception) {
        this.name = exception.getClass().getName();
        this.exception = exception;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        Packet.writeString(buf, name);
        writeException(buf, exception);
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
        name = Packet.readString(buf);
        exception = readException(buf);
    }

    private void writeException(ByteBuf buf, Exception exception) throws IOException {
        Packet.writeString(buf, exception.getMessage());
        StackTraceElement[] elements = exception.getStackTrace();
        Packet.writeVarInt(buf, elements.length);
        for (StackTraceElement element : elements)
            writeStackTraceElement(buf, element);
    }

    private Exception readException(ByteBuf buf) throws IOException {
        String message = Packet.readString(buf);
        int length = Packet.readVarInt(buf);
        StackTraceElement[] elements = new StackTraceElement[length];
        for (int i = 0; i < length; i++) {
            elements[i] = readStackTraceElement(buf);
        }

        Exception exception = new Exception(message);
        exception.setStackTrace(elements);
        return exception;
    }

    private void writeStackTraceElement(ByteBuf buf, StackTraceElement stackTraceElement) throws IOException {
        Packet.writeString(buf, stackTraceElement.getClassName());
        Packet.writeString(buf, stackTraceElement.getMethodName());
        Packet.writeString(buf, stackTraceElement.getFileName());
        buf.writeInt(stackTraceElement.getLineNumber());
    }

    private StackTraceElement readStackTraceElement(ByteBuf buf) throws IOException {
        String declaringClass = Packet.readString(buf);
        String methodName = Packet.readString(buf);
        String fileName = Packet.readString(buf);
        int lineNumber = buf.readInt();
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }

    @Override
    public String toString() {
        return "ExceptionPacket{" +
                "name='" + name + '\'' +
                ", exception=" + exception +
                '}';
    }
}
