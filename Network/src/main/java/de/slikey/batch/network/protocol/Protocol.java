package de.slikey.batch.network.protocol;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.ClassPath;
import gnu.trove.TObjectIntHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Kevin Carstens
 * @since 21.04.2015
 */
public class Protocol {

    private static final Logger logger = LogManager.getLogger(Protocol.class.getSimpleName());
    private static final int PROTOCOL_SIZE = Byte.MAX_VALUE;
    private static final List<Class<? extends Packet>> PACKETS = new ArrayList<>(PROTOCOL_SIZE);
    private static final Constructor[] CONSTRUCTORS = new Constructor[PROTOCOL_SIZE];
    private static final TObjectIntHashMap CLASS_ID_MAP = new TObjectIntHashMap(PROTOCOL_SIZE);
    private static int size = 0;
    private static int protocolHash = 0;
    private static boolean initialized = false;

    public static int getSize() {
        return size;
    }

    public static ImmutableList<Class<? extends Packet>> getPackets() {
        return ImmutableList.copyOf(PACKETS);
    }

    @SuppressWarnings("unchecked")
    public static void initialize() throws IOException {
        logger.info("Initialize Protocol...");
        final ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());

        String report = "Registered Packets to Protocol: \n{\n";
        for (ClassPath.ResourceInfo resourceInfo : classPath.getResources()) {
            try {
                String className = resourceInfo.toString();
                if (className.contains(".Packet")) {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.getSuperclass() == Packet.class) {
                        int index = register((Class<? extends Packet>) clazz);
                        report += "\t{id=" + index + ", name='" + clazz.getName() + "'},\n";
                    }
                }
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        report = report.substring(0, report.length() - 2) + "\n}";

        logger.debug(report + " (Hash: " + protocolHash + ")");
        initialized = true;
        logger.info("Successfully initialized Protocol!");
    }

    private static int register(Class<? extends Packet> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            int index = size++;
            PACKETS.add(clazz);
            CONSTRUCTORS[index] = constructor;
            CLASS_ID_MAP.put(clazz, index);

            protocolHash = 31 * protocolHash + index;
            protocolHash = 31 * protocolHash + clazz.getSimpleName().hashCode();

            return index;
        } catch (NoSuchMethodException e) {
            logger.error("Could not find empty Constructor for " + clazz.getSimpleName(), e);
        }
        return -1;
    }

    public static int getProtocolHash() {
        if (!initialized) {
            throw new IllegalStateException("Protocol has not been initialized!");
        }
        return protocolHash;
    }

    public static int getId(Class<? extends Packet> clazz) {
        return CLASS_ID_MAP.get(clazz);
    }

    public static Packet getPacket(int id) throws BadPacketException {
        if (!initialized) {
            throw new IllegalStateException("Protocol has not been initialized!");
        }

        try {
            Constructor<?> constructor = CONSTRUCTORS[id];
            if (constructor != null) {
                Packet packet = (Packet) constructor.newInstance();
                packet.setId(id);
                return packet;
            }
        } catch (IndexOutOfBoundsException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BadPacketException(e);
        }
        throw new BadPacketException("Invalid Packet ID! Given: " + id);
    }

}
