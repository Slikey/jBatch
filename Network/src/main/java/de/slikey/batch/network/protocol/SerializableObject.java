package de.slikey.batch.network.protocol;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

/**
 * @author Kevin
 * @since 06.09.2015
 */
public interface SerializableObject {

    void read(ByteBuf buf) throws IOException;

    void write(ByteBuf buf) throws IOException;

}
