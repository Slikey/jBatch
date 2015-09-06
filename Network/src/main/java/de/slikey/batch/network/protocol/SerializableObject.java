package de.slikey.batch.network.protocol;

import java.io.IOException;

/**
 * @author Kevin
 * @since 06.09.2015
 */
public interface SerializableObject {

    void read(BufferWrapper buf) throws IOException;

    void write(BufferWrapper buf) throws IOException;

}
