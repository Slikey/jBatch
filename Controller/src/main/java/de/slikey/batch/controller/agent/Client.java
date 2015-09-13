package de.slikey.batch.controller.agent;

import de.slikey.batch.controller.monitoring.HealthMonitor;
import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.protocol.PacketHealthStatus;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.management.Agent;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class Client {

    private static final Logger logger = LogManager.getLogger(Agent.class.getSimpleName());

    private final Channel channel;
    private final HealthMonitor healthMonitor;
    private String name;

    private ClientManager clientManager;

    public Client(Channel channel) {
        this.channel = channel;
        this.healthMonitor = new HealthMonitor();
    }

    public ClientManager getClientManager() {
        return clientManager;
    }

    public void setClientManager(ClientManager clientManager) {
        this.clientManager = clientManager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Channel getChannel() {
        return channel;
    }

    public void sendPacket(Packet packet) {
        channel.writeAndFlush(packet);
    }

    public void connected() {
    }

    public void disconnected() {

    }

    public void handleHealthStatus(PacketHealthStatus packet) {
        healthMonitor.addHealthStatus(packet);
    }

}
