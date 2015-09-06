package de.slikey.batch.agent;

import de.slikey.batch.network.protocol.HandlePacket;
import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.network.protocol.Protocol;
import de.slikey.batch.protocol.PacketHandshake;
import de.slikey.batch.protocol.PacketPing;
import de.slikey.batch.protocol.PacketPong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 30.04.2015
 */
public class AgentPacketHandler extends PacketHandler {

    private static final Logger logger = LogManager.getLogger(AgentPacketHandler.class.getSimpleName());

    private final AgentConnectionHandler connectionHandler;

    public AgentPacketHandler(AgentConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public AgentConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    @HandlePacket
    public void handle(PacketPing packet) {
        logger.debug(packet);
        getConnectionHandler().getInitializer().getBatchAgent().sendPacket(PacketPong.create(packet));
    }

    @HandlePacket
    public void handle(PacketPong packet) {
        logger.debug(packet);
    }

    @HandlePacket
    public void handle(PacketHandshake packet) {
        logger.info("Received handshake..");
        if (packet.getVersion() == Protocol.getProtocolHash()) {
            logger.info("Versions match! Sending auth-information...");
        } else {
            logger.info("Versions mismatch! Shutting down!");
            System.exit(0);
        }
    }

}
