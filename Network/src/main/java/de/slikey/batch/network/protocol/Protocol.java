package de.slikey.batch.network.protocol;

import de.slikey.batch.network.protocol.packet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

/**
 * @author Kevin Carstens
 * @since 21.04.2015
 */
public class Protocol {

    private static final Logger logger = LogManager.getLogger(Protocol.class);
    private static final Constructor[] packets = new Constructor[Byte.MAX_VALUE];
    private static final ArrayList<Class<? extends Packet>> packetIds = new ArrayList<>();
    private static boolean initialized = false;

    public static void initialize() {
        register(HandshakePacket.class);
        register(HealthStatusPacket.class);
        register(PingPacket.class);
        register(PongPacket.class);
        register(KeepAlivePacket.class);
        register(AuthResponsePacket.class);

        register(AgentInformationPacket.class);

        initialized = true;
    }

    private static void register(Class<? extends Packet> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            packetIds.add(clazz);
            packets[packetIds.indexOf(clazz)] = constructor;
        } catch (NoSuchMethodException e) {
            logger.error(e);
        }
    }

    public static int getId(Class<? extends Packet> clazz) {
        return packetIds.indexOf(clazz);
    }

    public static Packet getPacket(int id) throws BadPacketException {
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
