package de.slikey.batch.controller.job;

/**
 * @author Kevin Carstens
 * @since 24.04.2015
 */
public class JobScheduleInformation {

    private final long startTime;

    public JobScheduleInformation(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTime() {
        return startTime;
    }

}
