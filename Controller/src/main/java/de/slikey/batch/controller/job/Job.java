package de.slikey.batch.controller.job;

import de.slikey.batch.network.protocol.packet.JobExecutePacket;

import java.util.Random;
import java.util.UUID;

/**
 * @author Kevin Carstens
 * @since 23.04.2015
 */
public class Job {

    private static final Random random = new Random();
    private final UUID uuid;
    private final String command;

    public Job(String command) {
        this.uuid = new UUID(System.nanoTime(), random.nextLong());
        this.command = command;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCommand() {
        return command;
    }

    public JobExecutePacket getPacket() {
        return new JobExecutePacket(uuid, command);
    }
}
