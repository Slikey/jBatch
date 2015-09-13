package de.slikey.batch.controller.monitoring;

import de.slikey.batch.controller.MainController;
import de.slikey.batch.network.common.TickingManager;
import de.slikey.batch.protocol.PacketHealthStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Kevin Carstens
 * @since 27.04.2015
 */
public class HealthManager  extends TickingManager {

    private static final Logger logger = LogManager.getLogger(HealthManager.class.getSimpleName());

    private final MainController mainController;
    private final HealthMonitor healthMonitor;
    private final String started;
    private long lastUptimeReport;

    public HealthManager(MainController mainController) {
        super(mainController, 1000);
        this.mainController = mainController;
        this.healthMonitor = new HealthMonitor();
        this.started = new SimpleDateFormat("EEEE 'the' dd.MM.YY 'at' HH:mm:ss zzz").format(new Date());
        this.lastUptimeReport = Long.MIN_VALUE;
    }

    public MainController getMainController() {
        return mainController;
    }

    @Override
    protected void onTick(double deltaSeconds) {
        PacketHealthStatus packet = PacketHealthStatus.create();
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
    }

}
