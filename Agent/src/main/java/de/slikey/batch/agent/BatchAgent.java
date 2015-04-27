package de.slikey.batch.agent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.slikey.batch.network.client.NIOClient;
import de.slikey.batch.network.protocol.ConnectionHandler;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.network.protocol.PacketHandler;
import de.slikey.batch.network.protocol.Protocol;
import de.slikey.batch.protocol.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
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

    private static final Logger logger = LogManager.getLogger(BatchAgent.class.getSimpleName());
    public static final Random RANDOM = new Random();
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
                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                        logger.info("Connected to Controller! (" + ctx.channel().remoteAddress() + ")");
                        super.channelActive(ctx);
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        logger.info("Disconnected from Controller! (" + ctx.channel().remoteAddress() + ")");
                        super.channelInactive(ctx);
                    }

                    @Override
                    public PacketHandler newPacketHandler() {
                        return new PacketHandler() {

                            public void handle(PacketPing packet) {
                                logger.debug(packet);
                                socketChannel.writeAndFlush(PacketPong.create(packet));
                            }

                            public void handle(PacketPong packet) {
                                logger.debug(packet);
                            }

                            public void handle(HandshakePacket packet) {
                                logger.info("Received handshake..");
                                if (packet.getVersion() == Protocol.getProtocolHash()) {
                                    logger.info("Versions match! Sending auth-information...");
                                    socketChannel.writeAndFlush(new PacketAgentInformation("Worker_" + RANDOM.nextInt(Integer.MAX_VALUE), PacketAgentInformation.PASSWORD));
                                } else {
                                    logger.info("Versions mismatch! Shutting down!");
                                    System.exit(0);
                                }
                            }

                            public void handle(PacketAuthResponse packet) {
                                if (packet.getCode() == PacketAuthResponse.AuthResponseCode.SUCCESS) {
                                    logger.info("Authentication successful!");
                                } else {
                                    logger.info("Authentication unsuccessful! Shutting down...");
                                    System.exit(0);
                                }
                            }

                            public void handle(final PacketJobExecute packet) {
                                logger.info("Controller issued command to run job: " + packet);
                                THREAD_POOL.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Thread.sleep(RANDOM.nextInt(30 * 1000));
                                            int returnCode = 200;
                                            logger.info("Worker replied to job (" + packet.getUuid() + ") with Return-Code " + returnCode);
                                            socketChannel.writeAndFlush(new PacketJobResponse(packet.getUuid(), returnCode));
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
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
                        channel.writeAndFlush(new PacketKeepAlive());
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
                        channel.writeAndFlush(PacketHealthStatus.create());
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
                        //channel.writeAndFlush(PingPacket.create());
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
