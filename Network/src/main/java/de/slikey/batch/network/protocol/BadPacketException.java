package de.slikey.batch.network.protocol;

/**
 * @author Kevin
 * @since 17.04.2015
 */
public class BadPacketException extends Exception {

    public BadPacketException(String message) {
        super(message);
    }

    public BadPacketException(Throwable cause) {
        super(cause);
    }

}
