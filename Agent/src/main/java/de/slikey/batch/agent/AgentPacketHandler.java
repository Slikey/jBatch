package de.slikey.batch.agent;

import de.slikey.batch.agent.execution.JobExecutor;
import de.slikey.batch.network.protocol.HandlePacket;
import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.network.protocol.Protocol;
import de.slikey.batch.protocol.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;

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
        connectionHandler.getInitializer().getBatchAgent().sendPacket(PacketPong.create(packet));
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
            connectionHandler.getInitializer().getBatchAgent().sendPacket(new PacketAgentInformation(
                    "Worker_" + new Random().nextInt(Integer.MAX_VALUE), // Username
                    PacketAgentInformation.PASSWORD) // Password
            );
        } else {
            logger.info("Versions mismatch! Shutting down!");
            System.exit(0);
        }
    }

    @HandlePacket
    public void handle(PacketAuthResponse packet) {
        if (packet.getCode() == PacketAuthResponse.AuthResponseCode.SUCCESS) {
            logger.info("Authentication successful!");
        } else {
            logger.info("Authentication unsuccessful! Shutting down...");
            System.exit(0);
        }
    }

    @HandlePacket
    public void handle(final PacketJobExecute packet) {
        logger.info("Controller issued command to run job: " + packet);
        final BatchAgent batchAgent = connectionHandler.getInitializer().getBatchAgent();
        batchAgent.getThreadPool().execute(new Runnable() {

            @Override
            public void run() {
                JobExecutor jobExecutor = new JobExecutor(batchAgent, packet.getUuid(), packet.getCommand());
                jobExecutor.run();
                logger.info("Worker replied to job (" + packet.getUuid() + ") with Return-Code " + jobExecutor.getExitCode());
                batchAgent.sendPacket(new PacketJobResponse(packet.getUuid(), jobExecutor.getExitCode()));
            }

        });
    }

}
