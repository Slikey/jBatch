package de.slikey.batch.controller.job;

import de.slikey.batch.controller.BatchController;
import de.slikey.batch.controller.agent.Agent;
import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.packet.JobResponsePacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Kevin Carstens
 * @since 24.04.2015
 */
public class JobManager {

    private static final Logger logger = LogManager.getLogger(JobManager.class.getSimpleName());

    private final BatchController batchController;
    private final List<Job> jobs;

    public JobManager(BatchController batchController) {
        this.batchController = batchController;
        this.jobs = new CopyOnWriteArrayList<>();
    }

    public BatchController getBatchController() {
        return batchController;
    }

    public Job getJobByUUID(UUID uuid) {
        if (uuid == null) throw new NullPointerException("UUID cannot be null!");
        for (Job job : jobs) {
            if (job.getUuid().equals(uuid))
                return job;
        }
        return null;
    }

    public void schedule(Job job) {
        if (getJobByUUID(job.getUuid()) == null) {
            job.setJobState(JobState.SCHEDULED);
            jobs.add(job);
            logger.info("Job (ID: " + job.getUuid() + " scheduled.");
        } else {
            logger.error("Job (ID: " + job.getUuid() + " already exists! Cannot schedule a job twice!");
        }
    }

    private void executeJob(Job job) {
        if (job.getJobState() == JobState.SCHEDULED) {
            Agent agent = batchController.getAgentManager().getHealthBalancer().getHealthiestAgent();
            if (agent == null) {
                throw new NullPointerException("No active Agent was found!");
            } else {
                job.setJobState(JobState.EXECUTING);
                Packet packet = job.getPacket();
                logger.info("Sending job to Agent '" + agent.getInformation().getName() + "': " + packet);
                agent.sendPacket(packet);
            }
        } else {
            throw new IllegalStateException("JobState must be " + JobState.SCHEDULED + "! Given: " + job.getJobState());
        }
    }

    public void handleJobResponse(JobResponsePacket packet) {
        UUID uuid = packet.getUuid();
        Job job = getJobByUUID(uuid);
        if (job == null) {
            logger.error("Received " + packet.getClass().getSimpleName() + " with unknown JobID! Given: " + uuid.toString());
        } else {
            logger.info("Received JobResponse for JobID (" + uuid.toString() + "). Calling callback...");
            job.getCallback().response(packet);
        }
    }

    public void tick() {
        if (jobs.size() > 0 && batchController.getAgentManager().getAgents().size() == 0) {
            logger.warn("No active Agent found. There are " + jobs.size() + " job(s) pending to be executed!");
            return;
        }
        for (Job job : jobs) {
            if (job.getJobState() == JobState.SCHEDULED) {
                JobScheduleInformation scheduleInformation = job.getScheduleInformation();
                if (scheduleInformation == null) {
                    logger.info("Job (ID: " + job.getUuid() + ") has no dependencies. Executing...");
                    executeJob(job);
                } else {
                    if (System.currentTimeMillis() > scheduleInformation.getStartTime()) {
                        logger.info("Job (ID: " + job.getUuid() + ") resolved dependencies. Executing...");
                        executeJob(job);
                    }
                }
            }
        }
    }
}