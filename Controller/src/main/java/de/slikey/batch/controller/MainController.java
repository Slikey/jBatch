package de.slikey.batch.controller;

import de.slikey.batch.controller.agent.ClientManager;
import de.slikey.batch.controller.command.CommandManager;
import de.slikey.batch.controller.misc.KeepAliveManager;
import de.slikey.batch.controller.monitoring.HealthManager;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.network.server.NIOServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.TimeUnit;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class MainController extends NIOServer {

    private static final Logger logger = LogManager.getLogger(MainController.class.getSimpleName());

    public static void main(String[] args) {
        new MainController(8080).run();
    }

    private final ClientManager clientManager;
    private final HealthManager healthManager;
    private final KeepAliveManager keepAliveManager;
    private final CommandManager commandManager;

    public MainController(int port) {
        super(port, 32);
        this.clientManager = new ClientManager(this);
        this.healthManager = new HealthManager(this);
        this.keepAliveManager = new KeepAliveManager(this);
        this.commandManager = new CommandManager(this);
    }

    public ClientManager getClientManager() {
        return clientManager;
    }

    public HealthManager getHealthManager() {
        return healthManager;
    }

    public KeepAliveManager getKeepAliveManager() {
        return keepAliveManager;
    }

    @Override
    public void startApplication() throws InterruptedException {
        clientManager.start();
        healthManager.start();
        keepAliveManager.start();
        commandManager.start();
    }

    @Override
    protected void close() throws InterruptedException {
        super.close();

        getExecutorService().shutdownNow();
        getExecutorService().awaitTermination(5, TimeUnit.SECONDS);
    }

    @Override
    protected PacketChannelInitializer buildPacketChannelInitializer() {
        return new ControllerPacketChannelInitializer(this);
    }

}
