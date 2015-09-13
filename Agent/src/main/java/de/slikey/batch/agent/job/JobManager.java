package de.slikey.batch.agent.job;

import de.slikey.batch.agent.MainClient;
import de.slikey.batch.network.common.TickingManager;
import de.slikey.batch.protocol.PacketJobStart;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Kevin
 * @since 13.09.2015
 */
public class JobManager extends TickingManager {

    private final MainClient mainClient;
    private final Map<UUID, Job> jobMap;

    public JobManager(MainClient mainClient) {
        super(mainClient, 10000);
        this.mainClient = mainClient;
        this.jobMap = new HashMap<>();
    }

    @Override
    protected void onTick(double deltaSeconds) {
        System.out.println("Running " + jobMap.size() + " jobs.");
    }

    public void accept(PacketJobStart packet) {
        UUID uuid = packet.getUuid();
        String command = packet.getCommand();
        Job job = new Job(this, mainClient, uuid, command);
        mainClient.getExecutorService().submit(job::run);
    }

    public void start(Job job) {
        jobMap.put(job.getUuid(), job);
        System.out.println("Started job: " + job.getCommand() + " with uuid " + job.getUuid());
    }

    public void stop(Job job) {
        jobMap.remove(job.getUuid());
        System.out.println("Stopped job: " + job.getCommand() + " with uuid " + job.getUuid());
    }

}
