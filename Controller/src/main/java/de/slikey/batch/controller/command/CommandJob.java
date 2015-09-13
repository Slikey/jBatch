package de.slikey.batch.controller.command;

import de.slikey.batch.controller.MainController;
import de.slikey.batch.controller.agent.Client;
import de.slikey.batch.protocol.PacketJobStart;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;

/**
 * @author Kevin
 * @since 09.09.2015
 */
public class CommandJob extends Command {

    private static final Logger logger = LogManager.getLogger(CommandJob.class.getSimpleName());

    public CommandJob(MainController mainController) {
        super(mainController, "job", null);
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length >= 3) {
            String target = args[1];
            Client client = getMainController().getClientManager().getClient(target);
            if (client == null) {
                logger.info("Could not find client by name of '" + target + "'.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    sb.append(args[i]);
                }
                PacketJobStart packet = new PacketJobStart(UUID.randomUUID(), sb.toString());
                client.sendPacket(packet);
                logger.info("Job started on '" + target + "'.");
            }
            return true;
        }
        return false;
    }

    @Override
    public String getUsage() {
        return getLabel() + " <client> <command>";
    }

}
