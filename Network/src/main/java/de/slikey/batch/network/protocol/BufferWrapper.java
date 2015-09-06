package de.slikey.batch.network.protocol;

import io.netty.buffer.ByteBuf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * @author Kevin
 * @since 06.09.2015
 */
public class BufferWrapper {

    public static final Charset charset = Charset.forName("UTF-8");
    public static final int LIMIT_STRING_LIST_SIZE = Short.MAX_VALUE;

    private final ByteBuf buf;

    public BufferWrapper(ByteBuf buf) {
        this.buf = buf;
    }

    public ByteBuf getHandle() {
        return buf;
    }

    public void writeByte(byte b) {
        buf.writeByte(b);
    }

    public void writeByte(int i) {
        buf.writeByte(i);
    }

    public byte readByte() {
        return buf.readByte();
    }

    public void writeShort(short s) {
        buf.writeShort(s);
    }

    public short readShort() {
        return buf.readShort();
    }

    public void writeInt(int i) {
        buf.writeInt(i);
    }

    public int readInt() {
        return buf.readInt();
    }

    public void writeLong(long l) {
        buf.writeLong(l);
    }

    public long readLong() {
        return buf.readLong();
    }

    public void writeFloat(float f) {
        buf.writeFloat(f);
    }

    public float readFloat() {
        return buf.readFloat();
    }

    public void writeDouble(double d) {
        buf.writeDouble(d);
    }

    public double readDouble() {
        return buf.readDouble();
    }

    public void writeBytes(byte[] bytes) {
        buf.writeBytes(bytes);
    }

    public void readBytes(byte[] bytes) {
        buf.readBytes(bytes);
    }

    public void writeUUID(UUID uuid) {
        writeLong(uuid.getMostSignificantBits());
        writeLong(uuid.getLeastSignificantBits());
    }

    public UUID readUUID() {
        long most = readLong();
        long least = readLong();
        return new UUID(most, least);
    }

    public void writeByteArray(byte[] bytes) throws IOException {
        if (bytes.length > Short.MAX_VALUE) {
            throw new IOException("Byte-Array bigger than " + Short.MAX_VALUE + ". Given: " + bytes.length);
        }
        writeVarInt(bytes.length);
        writeBytes(bytes);
    }

    public byte[] readByteArray() throws IOException {
        int length = readVarInt();
        if (length < 0) {
            throw new IOException("Byte-Array length is negative. Given: " + length);
        }
        byte[] bytes = new byte[length];
        readBytes(bytes);
        return bytes;
    }

    public void writeString(String string) throws IOException {
        writeByteArray(string.getBytes(charset));
    }

    public String readString() throws IOException {
        return new String(readByteArray(), charset);
    }

    public void writeStringList(List<String> strings) throws IOException {
        int length = strings.size();
        if (length > LIMIT_STRING_LIST_SIZE) {
            throw new IOException("String list size is greater than limit (" + LIMIT_STRING_LIST_SIZE + "). Given: " + length);
        }
        writeVarInt(length);
        for (String string : strings)
            writeString(string);
    }

    public List<String> readStringList() throws IOException {
        int length = readVarInt();
        if (length > LIMIT_STRING_LIST_SIZE) {
            throw new IOException("String list size is greater than limit (" + LIMIT_STRING_LIST_SIZE + "). Given: " + length);
        }
        List<String> strings = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            strings.add(readString());
        }
        return strings;
    }

    public byte[] compress(byte[] uncompressed) throws IOException {
        return compress(uncompressed, Deflater.DEFAULT_COMPRESSION);
    }

    public byte[] compress(byte[] uncompressed, int level) throws IOException {
        Deflater deflater = new Deflater();
        deflater.setInput(uncompressed);
        deflater.setLevel(level);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(uncompressed.length);
        try {
            deflater.finish();
            byte[] buffer = new byte[1024];
            while (!deflater.finished()) {
                int count = deflater.deflate(buffer);
                baos.write(buffer, 0, count);
            }

        } finally {
            deflater.end();
            baos.close();
        }
        return baos.toByteArray();
    }

    public byte[] decompress(byte[] compressed) throws IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(compressed);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(compressed.length)) {
            byte[] buffer = new byte[1024];
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                baos.write(buffer, 0, count);
            }

            return baos.toByteArray();
        } catch (DataFormatException e) {
            throw new IOException(e);
        } finally {
            inflater.end();

        }
    }

    public int readVarInt() throws IOException {
        return readVarInt(5);
    }

    public int readVarInt(int maxBytes) throws IOException {
        return readVarInt(buf, maxBytes);
    }

    public void writeVarInt(int value) {
        writeVarInt(buf, value);
    }

    public static int readVarInt(ByteBuf buf, int maxBytes) throws IOException {
        int out = 0;
        int bytes = 0;
        byte in;
        while (true) {
            in = buf.readByte();

            out |= (in & 0x7F) << (bytes++ * 7);

            if (bytes > maxBytes)
                throw new IOException("VarInt too big");

            if ((in & 0x80) != 0x80)
                break;
        }

        return out;
    }

    public static void writeVarInt(ByteBuf buf, int value) {
        int part;
        while (true) {
            part = value & 0x7F;

            value >>>= 7;
            if (value != 0)
                part |= 0x80;

            buf.writeByte(part);

            if (value == 0)
                break;
        }
    }


    public void writeException(Exception exception) throws IOException {
        writeString(exception.getMessage());
        StackTraceElement[] elements = exception.getStackTrace();
        writeVarInt(elements.length);
        for (StackTraceElement element : elements)
            writeStackTraceElement(element);
    }

    public Exception readException() throws IOException {
        String message = readString();
        int length = readVarInt();
        StackTraceElement[] elements = new StackTraceElement[length];
        for (int i = 0; i < length; i++) {
            elements[i] = readStackTraceElement();
        }

        Exception exception = new Exception(message);
        exception.setStackTrace(elements);
        return exception;
    }

    private void writeStackTraceElement(StackTraceElement stackTraceElement) throws IOException {
        writeString(stackTraceElement.getClassName());
        writeString(stackTraceElement.getMethodName());
        writeString(stackTraceElement.getFileName());
        writeInt(stackTraceElement.getLineNumber());
    }

    private StackTraceElement readStackTraceElement() throws IOException {
        String declaringClass = readString();
        String methodName = readString();
        String fileName = readString();
        int lineNumber = readInt();
        return new StackTraceElement(declaringClass, methodName, fileName, lineNumber);
    }

}
