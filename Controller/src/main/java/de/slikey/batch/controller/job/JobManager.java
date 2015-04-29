package de.slikey.batch.controller.job;

import de.slikey.batch.controller.BatchController;
import de.slikey.batch.controller.agent.Agent;
import de.slikey.batch.network.common.TickingManager;
import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.protocol.PacketJobResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Kevin Carstens
 * @since 24.04.2015
 */
public class JobManager extends TickingManager{

    private static final Logger logger = LogManager.getLogger(JobManager.class.getSimpleName());

    private final BatchController batchController;
    private final List<Job> jobs;

    public JobManager(BatchController batchController) {
        super(1000);
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

    public void handleJobResponse(PacketJobResponse packet) {
        UUID uuid = packet.getUuid();
        Job job = getJobByUUID(uuid);
        if (job == null) {
            logger.error("Received " + packet.getClass().getSimpleName() + " with unknown JobID! Given: " + uuid.toString());
        } else {
            logger.info("Received JobResponse for JobID (" + uuid.toString() + "). Calling callback...");
            job.getCallback().response(packet);
            jobs.remove(job);
        }
    }

    public void onAgentRemove(Agent agent) {
        for (Job job : jobs) {
            if (job.getJobState() == JobState.EXECUTING && job.getAgent() == agent) {
                logger.error("Agent (" + agent.getInformation().getName() + ") disconnected while working on Job (" + job.toString() + ")!");
                handleJobResponse(new PacketJobResponse(job.getUuid(), -1));
            }
        }
    }

    public int getJobs(JobState jobState) {
        int i = 0;
        for (Job job : jobs) {
            if (job.getJobState() == jobState) {
                i++;
            }
        }
        return i;
    }

    @Override
    protected void onTick(double deltaSeconds) {
        int pendingJobs = getJobs(JobState.SCHEDULED);
        if (pendingJobs > 0 && batchController.getAgentManager().getAgents().size() == 0) {
            logger.warn("No active Agent found. There are " + pendingJobs + " job(s) pending to be executed!");
            return;
        }
        for (Job job : jobs) {
            if (job.getJobState() == JobState.SCHEDULED) {
                JobScheduleInformation scheduleInformation = job.getScheduleInformation();
                if (scheduleInformation == null) {
                    logger.info("Job (ID: " + job.getUuid() + ") has no dependencies. Executing...");
                    executeJob(job);
                } else {
                    if (scheduleInformation.isResolved(System.currentTimeMillis(), new HashSet<UUID>())) {
                        logger.info("Job (ID: " + job.getUuid() + ") resolved dependencies. Executing...");
                        executeJob(job);
                    }
                }
            }
        }
    }

}
