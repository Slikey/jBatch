package de.slikey.batch.network.protocol;

import de.slikey.batch.network.protocol.packet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Kevin Carstens
 * @since 21.04.2015
 */
public class Protocol {

    private static final Logger logger = LogManager.getLogger(Protocol.class);
    private static final Constructor[] packets = new Constructor[Byte.MAX_VALUE];
    private static boolean initialized = false;

    public static void initialize() {
        register(Packet1Handshake.class);
        register(Packet2HealthStatus.class);
        register(Packet4Ping.class);
        register(Packet5Pong.class);
        register(Packet6KeepAlive.class);
        register(Packet8AuthResponse.class);

        register(Packet40AgentInformation.class);

        initialized = true;
    }

    private static void register(Class<? extends Packet> clazz) {
        try {
            Packet packet = clazz.newInstance();
            packets[packet.getId()] = clazz.getConstructor();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            logger.error(e);
        }
    }

    public static Packet byId(int id) throws BadPacketException {
        if (!initialized) {
            throw new IllegalStateException("Protocol has not been initialized!");
        }

        try {
            Constructor<?> packet = packets[id];
            if (packet != null)
                return (Packet) packet.newInstance();
        } catch (IndexOutOfBoundsException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BadPacketException(e);
        }
        throw new BadPacketException("Invalid Packet ID! Given: " + id);
    }

}
