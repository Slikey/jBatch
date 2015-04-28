package de.slikey.batch.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.slikey.batch.controller.agent.AgentManager;
import de.slikey.batch.controller.job.Job;
import de.slikey.batch.controller.job.JobManager;
import de.slikey.batch.controller.job.JobResponseCallback;
import de.slikey.batch.controller.job.JobScheduleInformation;
import de.slikey.batch.controller.monitoring.HealthManager;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.network.server.NIOServer;
import de.slikey.batch.protocol.PacketJobResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Kevin Carstens
 * @since 13.04.2015
 */
public class BatchController extends NIOServer {

    private static final Logger logger = LogManager.getLogger(BatchController.class.getSimpleName());
    public static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("BatchController-%s")
            .build());

    public static void main(String[] args) {
        new BatchController(8080).run();
    }

    private final AgentManager agentManager;
    private final JobManager jobManager;
    private final HealthManager healthManager;

    public BatchController(int port) {
        super(port);
        this.agentManager = new AgentManager(this);
        this.jobManager = new JobManager(this);
        this.healthManager = new HealthManager(this);
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    @Override
    public void startApplication() {
        agentManager.start(THREAD_POOL);
        jobManager.start(THREAD_POOL);
        healthManager.start(THREAD_POOL);

        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Random random = new Random();
                    while (true) {
                        final String command = "java -version";
                        Job job = new Job(command, new JobResponseCallback() {
                            @Override
                            public void response(PacketJobResponse response) {
                                if (response.getReturnCode() == 0) {
                                    logger.info("Job successfully executed! '" + command + "'");
                                } else {
                                    logger.info("Job failed to execute! '" + command + "'");
                                }
                            }
                        });
                        job.setScheduleInformation(new JobScheduleInformation(System.currentTimeMillis() + (random.nextInt(10) + 10) * 1000L));
                        jobManager.schedule(job);
                        Thread.sleep(random.nextInt(45 * 1000));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected PacketChannelInitializer buildPacketChannelInitializer() {
        return new ControllerPacketChannelInitializer(this);
    }

}
