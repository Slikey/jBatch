package de.slikey.batch.controller.agent;

import de.slikey.batch.controller.MainController;
import de.slikey.batch.network.common.TickingManager;
import de.slikey.batch.network.protocol.Packet;
import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class ClientManager extends TickingManager {

    private final MainController mainController;
    private final Map<SocketAddress, Client> clients;

    public ClientManager(MainController mainController) {
        super(mainController, 1000);
        this.mainController = mainController;
        this.clients = new HashMap<>();
    }

    public void addClient(Client client) {
        client.setClientManager(this);
        Client oldClient = clients.put(client.getChannel().remoteAddress(), client);
        if (oldClient != null) {
            oldClient.getChannel().close();
        }
        mainController.getKeepAliveManager().sendKeepAlive(client);
    }

    public void removeClient(Channel channel) {
        Client client = clients.remove(channel.remoteAddress());
    }

    public MainController getMainController() {
        return mainController;
    }

    public List<Client> getClients() {
        return new ArrayList<>(clients.values());
    }

    public Client getClient(String name) {
        for (Client client : clients.values()) {
            if (name.equalsIgnoreCase(client.getName())) {
                return client;
            }
        }
        return null;
    }

    public void broadcast(Packet packet) {
        for (Client client : clients.values()) {
            client.sendPacket(packet);
        }
    }

    @Override
    protected void onTick(double deltaSeconds) {

    }

}
