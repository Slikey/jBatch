package de.slikey.batch.agent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.slikey.batch.network.client.NIOClient;
import de.slikey.batch.network.protocol.ConnectionHandler;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.network.protocol.packet.*;
import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class BatchAgent extends NIOClient {

    private static final Logger logger = LogManager.getLogger(BatchAgent.class);
    public static final int VERSION = 1;
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("BatchAgent-%s")
            .build());

    public static void main(String[] args) throws InterruptedException {
        new BatchAgent("localhost", 8080).run();
    }

    public BatchAgent(String host, int port) {
        super(host, port);
    }

    @Override
    protected PacketChannelInitializer buildPacketChannelInitializer() {
        return new PacketChannelInitializer() {

            @Override
            protected ConnectionHandler newConnectionHandler(final SocketChannel socketChannel) {
                return new ConnectionHandler() {
                    @Override
                    public PacketHandler newPacketHandler() {
                        return new PacketHandler() {

                            @Override
                            public void handle(PingPacket packet) {
                                logger.debug("Answering Ping: " + packet.toString());
                                socketChannel.writeAndFlush(PongPacket.create(packet));
                            }

                            @Override
                            public void handle(PongPacket packet) {
                                logger.debug("Received Pong: " + packet.toString());
                            }

                            @Override
                            public void handle(HandshakePacket packet) {
                                logger.info("cc Received handshake..");
                                if (packet.getVersion() == VERSION) {
                                    logger.info("cc Versions match! Sending auth-information...");
                                    socketChannel.writeAndFlush(new AgentInformationPacket(AgentInformationPacket.USERNAME, AgentInformationPacket.PASSWORD));
                                } else {
                                    logger.info("cc Versions mismatch! Shutting down!");
                                    System.exit(0);
                                }
                            }

                            @Override
                            public void handle(AuthResponsePacket packet) {
                                if (packet.getCode() == AuthResponsePacket.AuthResponseCode.SUCCESS) {
                                    logger.info("cc Authentication successful!");
                                } else {
                                    logger.info("cc Authentication unsuccessful! Shutting down...");
                                    System.exit(0);
                                }
                            }
                        };
                    }
                };
            }

            @Override
            protected ReadTimeoutHandler newReadTimeoutHandler(SocketChannel socketChannel) {
                return null;
            }
        };
    }

    @Override
    protected void startClient() throws InterruptedException {
        final Channel channel = getChannel();
        final Random random = new Random(System.nanoTime());
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (channel.isActive()) {
                        channel.writeAndFlush(new KeepAlivePacket());
                        Thread.sleep(random.nextInt(4000));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (channel.isActive()) {
                        channel.writeAndFlush(HealthStatusPacket.create());
                        Thread.sleep(random.nextInt(1000));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                try {
                    while (channel.isActive()) {
                        String line = reader.readLine();
                        if (line == null)
                            continue;
                        if (line.equals("/quit")) {
                            synchronized (BatchAgent.this) {
                                BatchAgent.this.notify();
                            }
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    while (channel.isActive()) {
                        channel.writeAndFlush(PingPacket.create());
                        Thread.sleep(random.nextInt(30000));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        synchronized (this) {
            wait();
        }
    }

}
