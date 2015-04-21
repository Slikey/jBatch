package de.slikey.batch.network.protocol.packet;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;
import de.slikey.batch.network.protocol.Packet;
import de.slikey.batch.network.protocol.PacketHandler;
import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.lang.management.*;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class Packet2HealthStatus extends Packet {

    private static final OperatingSystemMXBean systemBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private static final ThreadMXBean threadBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
    private static final ClassLoadingMXBean classBean = ManagementFactory.getClassLoadingMXBean();

    public static Packet2HealthStatus create() {
        Packet2HealthStatus packet = new Packet2HealthStatus();

        packet.committedVirtualMemorySize = systemBean.getCommittedVirtualMemorySize();
        packet.freePhysicalMemorySize = systemBean.getFreePhysicalMemorySize();
        packet.freeSwapSpaceSize = systemBean.getFreeSwapSpaceSize();
        packet.processCpuLoad = systemBean.getProcessCpuLoad();
        packet.processCpuTime = systemBean.getProcessCpuTime();
        packet.systemCpuLoad = systemBean.getSystemCpuLoad();
        packet.totalPhysicalMemorySize = systemBean.getTotalPhysicalMemorySize();
        packet.totalSwapSpaceSize = systemBean.getTotalSwapSpaceSize();

        packet.name = runtimeBean.getName();
        packet.vmVersion = runtimeBean.getVmVersion();
        packet.uptime = runtimeBean.getUptime();
        packet.startTime = runtimeBean.getStartTime();

        packet.heapMemoryUsage = memoryBean.getHeapMemoryUsage();
        packet.nonHeapMemoryUsage = memoryBean.getNonHeapMemoryUsage();
        packet.objectPendingFinalizationCount = memoryBean.getObjectPendingFinalizationCount();

        packet.currentThreadCpuTime = threadBean.getCurrentThreadCpuTime();
        packet.currentThreadUserTime = threadBean.getCurrentThreadUserTime();
        packet.threadCount = threadBean.getThreadCount();
        packet.daemonThreadCount = threadBean.getDaemonThreadCount();
        packet.peakThreadCount = threadBean.getPeakThreadCount();
        packet.totalStartedThreadCount = threadBean.getTotalStartedThreadCount();

        packet.loadedClassCount = classBean.getLoadedClassCount();
        packet.totalLoadedClassCount = classBean.getTotalLoadedClassCount();
        packet.unloadedClassCount = classBean.getUnloadedClassCount();

        return packet;
    }

    // Information of System
    private long committedVirtualMemorySize;
    private long freePhysicalMemorySize;
    private long freeSwapSpaceSize;
    private double processCpuLoad;
    private long processCpuTime;
    private double systemCpuLoad;
    private long totalPhysicalMemorySize;
    private long totalSwapSpaceSize;

    // Information on Runtime
    private String name;
    private String vmVersion;
    private long uptime;
    private long startTime;

    // Information on Memory
    private MemoryUsage heapMemoryUsage;
    private MemoryUsage nonHeapMemoryUsage;
    private int objectPendingFinalizationCount;

    // Information on Threads
    private long currentThreadCpuTime;
    private long currentThreadUserTime;
    private int threadCount;
    private int daemonThreadCount;
    private int peakThreadCount;
    private long totalStartedThreadCount;

    // Information on Classloading
    private int loadedClassCount;
    private long totalLoadedClassCount;
    private long unloadedClassCount;

    public Packet2HealthStatus() {

    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void write(ByteBuf buf) throws IOException {
        // Information of System
        buf.writeLong(committedVirtualMemorySize);
        buf.writeLong(freePhysicalMemorySize);
        buf.writeLong(freeSwapSpaceSize);
        buf.writeDouble(processCpuLoad);
        buf.writeLong(processCpuTime);
        buf.writeDouble(systemCpuLoad);
        buf.writeLong(totalPhysicalMemorySize);
        buf.writeLong(totalSwapSpaceSize);

        // Information on Runtime
        writeString(buf, name);
        writeString(buf, vmVersion);
        buf.writeLong(uptime);
        buf.writeLong(startTime);

        // Information on Memory
        writeMemoryUsage(buf, heapMemoryUsage);
        writeMemoryUsage(buf, nonHeapMemoryUsage);
        buf.writeInt(objectPendingFinalizationCount);

        // Information on Threads
        buf.writeLong(currentThreadCpuTime);
        buf.writeLong(currentThreadUserTime);
        buf.writeInt(threadCount);
        buf.writeInt(daemonThreadCount);
        buf.writeInt(peakThreadCount);
        buf.writeLong(totalStartedThreadCount);

        // Information on Classloading
        buf.writeInt(loadedClassCount);
        buf.writeLong(totalLoadedClassCount);
        buf.writeLong(unloadedClassCount);
    }

    @Override
    public Packet read(ByteBuf buf) throws IOException {
        committedVirtualMemorySize = buf.readLong();
        freePhysicalMemorySize = buf.readLong();
        freeSwapSpaceSize = buf.readLong();
        processCpuLoad = buf.readDouble();
        processCpuTime = buf.readLong();
        systemCpuLoad = buf.readDouble();
        totalPhysicalMemorySize = buf.readLong();
        totalSwapSpaceSize = buf.readLong();

        name = readString(buf);
        vmVersion = readString(buf);
        uptime = buf.readLong();
        startTime = buf.readLong();

        heapMemoryUsage = readMemoryUsage(buf);
        nonHeapMemoryUsage = readMemoryUsage(buf);
        objectPendingFinalizationCount = buf.readInt();

        currentThreadCpuTime = buf.readLong();
        currentThreadUserTime = buf.readLong();
        threadCount = buf.readInt();
        daemonThreadCount = buf.readInt();
        peakThreadCount = buf.readInt();
        totalStartedThreadCount = buf.readLong();

        loadedClassCount = buf.readInt();
        totalLoadedClassCount = buf.readLong();
        unloadedClassCount = buf.readLong();

        return this;
    }

    private void writeMemoryUsage(ByteBuf buf, MemoryUsage memoryUsage) throws IOException {
        buf.writeLong(memoryUsage.getInit());
        buf.writeLong(memoryUsage.getUsed());
        buf.writeLong(memoryUsage.getCommitted());
        buf.writeLong(memoryUsage.getMax());
    }

    private MemoryUsage readMemoryUsage(ByteBuf buf) throws IOException {
        long init = buf.readLong();
        long used = buf.readLong();
        long committed = buf.readLong();
        long max = buf.readLong();
        return new MemoryUsage(init, used, committed, max);
    }

    @Override
    public void handle(PacketHandler packetListener) {
        packetListener.handle(this);
    }

    @Override
    public String toString() {
        return "Packet2HealthStatus{" +
                "committedVirtualMemorySize=" + committedVirtualMemorySize +
                ", freePhysicalMemorySize=" + freePhysicalMemorySize +
                ", freeSwapSpaceSize=" + freeSwapSpaceSize +
                ", processCpuLoad=" + processCpuLoad +
                ", processCpuTime=" + processCpuTime +
                ", systemCpuLoad=" + systemCpuLoad +
                ", totalPhysicalMemorySize=" + totalPhysicalMemorySize +
                ", totalSwapSpaceSize=" + totalSwapSpaceSize +
                ", name='" + name + '\'' +
                ", vmVersion='" + vmVersion + '\'' +
                ", uptime=" + uptime +
                ", startTime=" + startTime +
                ", heapMemoryUsage={" + heapMemoryUsage + '}' +
                ", nonHeapMemoryUsage={" + nonHeapMemoryUsage + '}' +
                ", objectPendingFinalizationCount=" + objectPendingFinalizationCount +
                ", currentThreadCpuTime=" + currentThreadCpuTime +
                ", currentThreadUserTime=" + currentThreadUserTime +
                ", threadCount=" + threadCount +
                ", daemonThreadCount=" + daemonThreadCount +
                ", peakThreadCount=" + peakThreadCount +
                ", totalStartedThreadCount=" + totalStartedThreadCount +
                ", loadedClassCount=" + loadedClassCount +
                ", totalLoadedClassCount=" + totalLoadedClassCount +
                ", unloadedClassCount=" + unloadedClassCount +
                '}';
    }

}
