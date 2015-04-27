package de.slikey.batch.controller.job;

import de.slikey.batch.controller.agent.Agent;
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
    private final JobResponseCallback callback;
    private JobState jobState;
    private JobScheduleInformation scheduleInformation;
    private Agent agent;

    public Job(String command, JobResponseCallback callback) {
        this.uuid = new UUID(System.nanoTime(), random.nextLong());
        this.command = command;
        this.callback = callback;
        this.jobState = JobState.SCHEDULED;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getCommand() {
        return command;
    }

    public JobState getJobState() {
        return jobState;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public void setJobState(JobState jobState) {
        this.jobState = jobState;
    }

    public JobScheduleInformation getScheduleInformation() {
        return scheduleInformation;
    }

    public void setScheduleInformation(JobScheduleInformation scheduleInformation) {
        this.scheduleInformation = scheduleInformation;
    }

    public JobExecutePacket getPacket() {
        return new JobExecutePacket(uuid, command);
    }

    public JobResponseCallback getCallback() {
        return callback;
    }
}
