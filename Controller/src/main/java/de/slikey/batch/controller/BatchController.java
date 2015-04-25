package de.slikey.batch.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.slikey.batch.controller.agent.AgentManager;
import de.slikey.batch.controller.job.Job;
import de.slikey.batch.controller.job.JobManager;
import de.slikey.batch.controller.job.JobResponseCallback;
import de.slikey.batch.controller.job.JobScheduleInformation;
import de.slikey.batch.controller.monitoring.HealthMonitor;
import de.slikey.batch.network.protocol.PacketChannelInitializer;
import de.slikey.batch.network.protocol.packet.HealthStatusPacket;
import de.slikey.batch.network.protocol.packet.JobResponsePacket;
import de.slikey.batch.network.server.NIOServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private final HealthMonitor healthMonitor;

    public BatchController(int port) {
        super(port);
        this.agentManager = new AgentManager(this);
        this.jobManager = new JobManager(this);
        this.healthMonitor = new HealthMonitor();
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    @Override
    public void startApplication() {
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        try {
                            agentManager.tick();
                        } catch (Exception e) {
                            logger.error("Exception occurred in AgentManager tick.", e);
                        }
                        try {
                            jobManager.tick();
                        } catch (Exception e) {
                            logger.error("Exception occurred in JobManager tick.", e);
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        THREAD_POOL.execute(new Runnable() {
            @Override
            public void run() {
                logger.info("Starting Application: Monitoring Health");
                long lastUptimeReport = Long.MIN_VALUE;
                try {
                    DateFormat dateFormat = new SimpleDateFormat("EEEE 'the' dd.MM.YY 'at' HH:mm:ss zzz");
                    String started = dateFormat.format(new Date());
                    while (true) {
                        HealthStatusPacket packet = HealthStatusPacket.create();
                        if (packet.getSystemCpuLoad() > 0.95) {
                            logger.warn("CPU Load is over 95%! Please check for issues! Alert. Alert.");
                        }
                        if (packet.getThreadCount() > 2000) {
                            logger.warn("Running more than 2000 threads! Please check for issues! Alert. Alert.");
                        }
                        if (packet.getFreePhysicalMemorySize() < 0.5 * 1024 * 1024 * 1024L) {
                            logger.warn("Less than 512MB is available on this machine! Please check for issues! Alert. Alert.");
                        }
                        if (packet.getUptime() > lastUptimeReport + 30 * 60 * 1000) {
                            logger.info("Uptime-Report: " + (packet.getUptime() / (60 * 1000)) + " minutes, Launch: " + started);
                            lastUptimeReport = packet.getUptime();
                        }
                        healthMonitor.addHealthStatus(packet);
                        Thread.sleep(1000);
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
                    Random random = new Random();
                    while (true) {
                        Thread.sleep(random.nextInt(25000));
                        final String command = "sh ua_" + (random.nextInt(8999) + 1000) + ".sh -range 0-" + random.nextInt(423);
                        Job job = new Job(command, new JobResponseCallback() {
                            @Override
                            public void response(JobResponsePacket response) {
                                logger.info("Job successfully executed! '" + command + "'");
                            }
                        });
                        if (random.nextBoolean())
                            job.setScheduleInformation(new JobScheduleInformation((random.nextInt(10) + 10) * 10 * 1000L));
                        jobManager.schedule(job);
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
