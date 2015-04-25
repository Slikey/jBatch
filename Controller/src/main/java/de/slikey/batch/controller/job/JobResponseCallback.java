package de.slikey.batch.controller.job;

import de.slikey.batch.network.protocol.packet.JobResponsePacket;

/**
 * @author Kevin Carstens
 * @since 24.04.2015
 */
public interface JobResponseCallback {

    public void response(JobResponsePacket response);

}
