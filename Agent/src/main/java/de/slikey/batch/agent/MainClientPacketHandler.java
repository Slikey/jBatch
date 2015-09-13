package de.slikey.batch.agent;

import de.slikey.batch.network.client.ClientPacketHandler;
import de.slikey.batch.network.protocol.HandlePacket;
import de.slikey.batch.protocol.PacketJobStart;
import de.slikey.batch.protocol.PacketPing;
import de.slikey.batch.protocol.PacketPong;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Kevin Carstens
 * @since 30.04.2015
 */
public class MainClientPacketHandler extends ClientPacketHandler<MainClient> {

    private static final Logger logger = LogManager.getLogger(MainClientPacketHandler.class.getSimpleName());

    public MainClientPacketHandler(MainClientConnectionHandler connectionHandler) {
        super(connectionHandler);
    }

    @HandlePacket
    public void handle(PacketPing packet) {
        logger.debug(packet);
        getClient().sendPacket(PacketPong.create(packet));
    }

    @HandlePacket
    public void handle(PacketPong packet) {
        logger.debug(packet);
    }

    @HandlePacket
    public void handle(final PacketJobStart packet) {
        final MainClient mainClient = getClient();
        mainClient.getExecutorService().submit(() -> mainClient.getJobManager().accept(packet));
    }

}
