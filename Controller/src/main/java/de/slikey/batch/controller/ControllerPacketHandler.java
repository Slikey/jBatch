package de.slikey.batch.controller;

import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.network.protocol.packet.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class ControllerPacketHandler extends PacketHandler {

    private static final Logger logger = LogManager.getLogger(ControllerPacketHandler.class.getSimpleName());
    private final ControllerConnectionHandler connectionHandler;

    public ControllerPacketHandler(ControllerConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public ControllerConnectionHandler getConnectionHandler() {
        return connectionHandler;
    }

    @Override
    public void handle(HealthStatusPacket packet) {
        connectionHandler.getAgent().handleHealthStatus(packet);
    }

    @Override
    public void handle(PingPacket packet) {
        logger.debug(packet);
        connectionHandler.getAgent().sendPacket(PongPacket.create(packet));
    }

    @Override
    public void handle(PongPacket packet) {
        logger.debug(packet);
    }

    @Override
    public void handle(ExceptionPacket packet) {
        logger.error("Exception thrown in connected Agent. (" + connectionHandler.getAgent().getInformation().getName() + ")", packet.getException());
    }

    @Override
    public void handle(KeepAlivePacket packet) {
    }

    @Override
    public void handle(AgentInformationPacket packet) {
        connectionHandler.getAgent().handleAgentInformation(packet);
    }

    @Override
    public void handle(JobResponsePacket packet) {
        connectionHandler.getInitializer().getBatchController().getJobManager().handleJobResponse(packet);
    }
}
