package de.slikey.batch.controller.job;

import de.slikey.batch.protocol.PacketJobResponse;

/**
 * @author Kevin Carstens
 * @since 24.04.2015
 */
public interface JobResponseCallback {

    public void response(PacketJobResponse response);

}
