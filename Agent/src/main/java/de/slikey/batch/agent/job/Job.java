package de.slikey.batch.agent.job;

import de.slikey.batch.agent.MainClient;
import de.slikey.batch.agent.executor.ProcessExecutor;

import java.util.UUID;

/**
 * @author Kevin
 * @since 13.09.2015
 */
public class Job extends ProcessExecutor {

    private final JobManager jobManager;

    public Job(JobManager jobManager, MainClient mainClient, UUID uuid, String command) {
        super(mainClient, uuid, command);
        this.jobManager = jobManager;
    }

    @Override
    protected void onStart() {
        super.onStart();
        jobManager.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        jobManager.stop(this);
    }


}
