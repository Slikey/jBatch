package de.slikey.batch.controller.job;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Kevin Carstens
 * @since 24.04.2015
 */
public class JobScheduleInformation {

    private final long startTime;
    private final Collection<UUID> dependencies;

    public JobScheduleInformation(long startTime, Collection<UUID> dependencies) {
        this.startTime = startTime;
        this.dependencies = dependencies;
    }

    public long getStartTime() {
        return startTime;
    }

    public Collection<UUID> getDependencies() {
        return dependencies;
    }

    public boolean isResolved(long currentTime, Collection<UUID> successfulJobs) {
        return isTimeResolved(currentTime) && isDependenciesResolved(successfulJobs);
    }

    public boolean isTimeResolved(long currentTime) {
        return currentTime >= startTime;
    }

    public boolean isDependenciesResolved(Collection<UUID> successfulJobs) {
        return dependencies == null || successfulJobs.containsAll(dependencies);
    }
}
