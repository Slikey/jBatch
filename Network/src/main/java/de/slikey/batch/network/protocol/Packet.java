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
 * @since 23.03.2015
 */
public abstract class Packet {

    public static final Charset charset = Charset.forName("UTF-8");
    public static final int LIMIT_STRING_LIST_SIZE = Short.MAX_VALUE;

    public abstract int getId();

    public abstract void write(ByteBuf buf) throws IOException;

    public abstract Packet read(ByteBuf buf) throws IOException;

    public static void writeUUID(ByteBuf buf, UUID uuid) {
        buf.writeLong(uuid.getMostSignificantBits());
        buf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf buf) {
        long most = buf.readLong();
        long least = buf.readLong();
        return new UUID(most, least);
    }

    public static void writeByteArray(ByteBuf buf, byte[] bytes) throws IOException {
        if (bytes.length > Short.MAX_VALUE) {
            throw new IOException("Byte-Array bigger than " + Short.MAX_VALUE + ". Given: " + bytes.length);
        }
        writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    public static byte[] readByteArray(ByteBuf buf) throws IOException {
        int length = readVarInt(buf);
        if (length < 0) {
            throw new IOException("Byte-Array length is negative. Given: " + length);
        }
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return bytes;
    }

    public static void writeString(ByteBuf buf, String string) throws IOException {
        writeByteArray(buf, string.getBytes(charset));
    }

    public static String readString(ByteBuf buf) throws IOException {
        return new String(readByteArray(buf), charset);
    }

    public static void writeStringList(ByteBuf buf, List<String> strings) throws IOException {
        int length = strings.size();
        if (length > LIMIT_STRING_LIST_SIZE) {
            throw new IOException("String list size is greater than limit (" + LIMIT_STRING_LIST_SIZE + "). Given: " + length);
        }
        writeVarInt(buf, length);
        for (String string : strings)
            writeString(buf, string);
    }

    public static List<String> readStringList(ByteBuf buf) throws IOException {
        int length = readVarInt(buf);
        if (length > LIMIT_STRING_LIST_SIZE) {
            throw new IOException("String list size is greater than limit (" + LIMIT_STRING_LIST_SIZE + "). Given: " + length);
        }
        List<String> strings = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            strings.add(readString(buf));
        }
        return strings;
    }

    public static byte[] compress(byte[] uncompressed) throws IOException {
        return compress(uncompressed, Deflater.DEFAULT_COMPRESSION);
    }

    public static byte[] compress(byte[] uncompressed, int level) throws IOException {
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

    public static byte[] decompress(byte[] compressed) throws IOException {
        Inflater inflater = new Inflater();
        inflater.setInput(compressed);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(compressed.length);
        try {
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
            baos.close();
        }
    }

    public static int readVarInt(ByteBuf buf) throws IOException {
        return readVarInt(buf, 5);
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

    public abstract void handle(PacketHandler packetListener);

}