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
        writeString(buf, name);
        writeException(buf, exception);
    }

    @Override
    public void read(ByteBuf buf) throws IOException {
        name = readString(buf);
        exception = readException(buf);
    }

    private void writeException(ByteBuf buf, Exception exception) throws IOException {
        writeString(buf, exception.getMessage());
        StackTraceElement[] elements = exception.getStackTrace();
        writeVarInt(buf, elements.length);
        for (StackTraceElement element : elements)
            writeStackTraceElement(buf, element);
    }

    private Exception readException(ByteBuf buf) throws IOException {
        String message = readString(buf);
        int length = readVarInt(buf);
        StackTraceElement[] elements = new StackTraceElement[length];
        for (int i = 0; i < length; i++) {
            elements[i] = readStackTraceElement(buf);
        }

        Exception exception = new Exception(message);
        exception.setStackTrace(elements);
        return exception;
    }

    private void writeStackTraceElement(ByteBuf buf, StackTraceElement stackTraceElement) throws IOException {
        writeString(buf, stackTraceElement.getClassName());
        writeString(buf, stackTraceElement.getMethodName());
        writeString(buf, stackTraceElement.getFileName());
        buf.writeInt(stackTraceElement.getLineNumber());
    }

    private StackTraceElement readStackTraceElement(ByteBuf buf) throws IOException {
        String declaringClass = readString(buf);
        String methodName = readString(buf);
        String fileName = readString(buf);
        int lineNumber = buf.readInt();
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "name='" + name + '\'' +
                ", exception=" + exception +
                '}';
    }
}
