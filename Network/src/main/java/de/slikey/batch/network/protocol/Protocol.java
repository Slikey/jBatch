package de.slikey.batch.network.protocol;

import com.google.common.reflect.ClassPath;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Set;

/**
 * @author Kevin Carstens
 * @since 21.04.2015
 */
public class Protocol {

    private static final Logger logger = LogManager.getLogger(Protocol.class.getSimpleName());
    private static final Constructor[] packets = new Constructor[Byte.MAX_VALUE];
    private static final ArrayList<Class<? extends Packet>> packetIds = new ArrayList<>();
    private static int protocolHash = 0;
    private static boolean initialized = false;

    public static void initialize() throws IOException {
        initialize("de.slikey.batch.network.protocol.packet");
    }

    @SuppressWarnings("unchecked")
    public static void initialize(String packagePath) throws IOException{
        logger.info("Initialize Protocol...");
        final ClassPath classPath = ClassPath.from(Thread.currentThread().getContextClassLoader());
        Set<ClassPath.ClassInfo> classes = classPath.getTopLevelClasses(packagePath);

        String report = "Registered Packets to Protocol: \n{\n";
        for (ClassPath.ClassInfo classInfo : classes) {
            Class<?> clazz = classInfo.load();
            if (clazz.getSuperclass() == Packet.class) {
                int index = register((Class<? extends Packet>) clazz);
                report += "\t{id=" + index + ", name='" + clazz.getName() + "'},\n";
            }
        }
        report = report.substring(0, report.length() - 2) + "\n}";
        protocolHash = report.hashCode();

        logger.debug(report + " (Hash: " + protocolHash + ")");
        initialized = true;
        logger.info("Successfully initialized Protocol!");
    }

    private static int register(Class<? extends Packet> clazz) {
        try {
            Constructor<?> constructor = clazz.getConstructor();
            constructor.setAccessible(true);
            packetIds.add(clazz);
            int index = packetIds.indexOf(clazz);
            packets[index] = constructor;
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
