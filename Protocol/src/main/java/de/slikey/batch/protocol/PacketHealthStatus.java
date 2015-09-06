package de.slikey.batch.protocol;

import com.sun.management.OperatingSystemMXBean;
import com.sun.management.ThreadMXBean;
import de.slikey.batch.network.protocol.BufferWrapper;
import de.slikey.batch.network.protocol.Packet;

import java.io.IOException;
import java.lang.management.*;

/**
 * @author Kevin Carstens
 * @since 19.04.2015
 */
public class PacketHealthStatus extends Packet {

    private static final OperatingSystemMXBean systemBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
    private static final RuntimeMXBean runtimeBean = ManagementFactory.getRuntimeMXBean();
    private static final MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
    private static final ThreadMXBean threadBean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
    private static final ClassLoadingMXBean classBean = ManagementFactory.getClassLoadingMXBean();

    public static PacketHealthStatus create() {
        PacketHealthStatus packet = new PacketHealthStatus();

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

    public PacketHealthStatus() {
        super();
    }

    public static OperatingSystemMXBean getSystemBean() {
        return systemBean;
    }

    public static RuntimeMXBean getRuntimeBean() {
        return runtimeBean;
    }

    public static MemoryMXBean getMemoryBean() {
        return memoryBean;
    }

    public static ThreadMXBean getThreadBean() {
        return threadBean;
    }

    public static ClassLoadingMXBean getClassBean() {
        return classBean;
    }

    public long getCommittedVirtualMemorySize() {
        return committedVirtualMemorySize;
    }

    public void setCommittedVirtualMemorySize(long committedVirtualMemorySize) {
        this.committedVirtualMemorySize = committedVirtualMemorySize;
    }

    public long getFreePhysicalMemorySize() {
        return freePhysicalMemorySize;
    }

    public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {
        this.freePhysicalMemorySize = freePhysicalMemorySize;
    }

    public long getFreeSwapSpaceSize() {
        return freeSwapSpaceSize;
    }

    public void setFreeSwapSpaceSize(long freeSwapSpaceSize) {
        this.freeSwapSpaceSize = freeSwapSpaceSize;
    }

    public double getProcessCpuLoad() {
        return processCpuLoad;
    }

    public void setProcessCpuLoad(double processCpuLoad) {
        this.processCpuLoad = processCpuLoad;
    }

    public long getProcessCpuTime() {
        return processCpuTime;
    }

    public void setProcessCpuTime(long processCpuTime) {
        this.processCpuTime = processCpuTime;
    }

    public double getSystemCpuLoad() {
        return systemCpuLoad;
    }

    public void setSystemCpuLoad(double systemCpuLoad) {
        this.systemCpuLoad = systemCpuLoad;
    }

    public long getTotalPhysicalMemorySize() {
        return totalPhysicalMemorySize;
    }

    public void setTotalPhysicalMemorySize(long totalPhysicalMemorySize) {
        this.totalPhysicalMemorySize = totalPhysicalMemorySize;
    }

    public long getTotalSwapSpaceSize() {
        return totalSwapSpaceSize;
    }

    public void setTotalSwapSpaceSize(long totalSwapSpaceSize) {
        this.totalSwapSpaceSize = totalSwapSpaceSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVmVersion() {
        return vmVersion;
    }

    public void setVmVersion(String vmVersion) {
        this.vmVersion = vmVersion;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public MemoryUsage getHeapMemoryUsage() {
        return heapMemoryUsage;
    }

    public void setHeapMemoryUsage(MemoryUsage heapMemoryUsage) {
        this.heapMemoryUsage = heapMemoryUsage;
    }

    public MemoryUsage getNonHeapMemoryUsage() {
        return nonHeapMemoryUsage;
    }

    public void setNonHeapMemoryUsage(MemoryUsage nonHeapMemoryUsage) {
        this.nonHeapMemoryUsage = nonHeapMemoryUsage;
    }

    public int getObjectPendingFinalizationCount() {
        return objectPendingFinalizationCount;
    }

    public void setObjectPendingFinalizationCount(int objectPendingFinalizationCount) {
        this.objectPendingFinalizationCount = objectPendingFinalizationCount;
    }

    public long getCurrentThreadCpuTime() {
        return currentThreadCpuTime;
    }

    public void setCurrentThreadCpuTime(long currentThreadCpuTime) {
        this.currentThreadCpuTime = currentThreadCpuTime;
    }

    public long getCurrentThreadUserTime() {
        return currentThreadUserTime;
    }

    public void setCurrentThreadUserTime(long currentThreadUserTime) {
        this.currentThreadUserTime = currentThreadUserTime;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getDaemonThreadCount() {
        return daemonThreadCount;
    }

    public void setDaemonThreadCount(int daemonThreadCount) {
        this.daemonThreadCount = daemonThreadCount;
    }

    public int getPeakThreadCount() {
        return peakThreadCount;
    }

    public void setPeakThreadCount(int peakThreadCount) {
        this.peakThreadCount = peakThreadCount;
    }

    public long getTotalStartedThreadCount() {
        return totalStartedThreadCount;
    }

    public void setTotalStartedThreadCount(long totalStartedThreadCount) {
        this.totalStartedThreadCount = totalStartedThreadCount;
    }

    public int getLoadedClassCount() {
        return loadedClassCount;
    }

    public void setLoadedClassCount(int loadedClassCount) {
        this.loadedClassCount = loadedClassCount;
    }

    public long getTotalLoadedClassCount() {
        return totalLoadedClassCount;
    }

    public void setTotalLoadedClassCount(long totalLoadedClassCount) {
        this.totalLoadedClassCount = totalLoadedClassCount;
    }

    public long getUnloadedClassCount() {
        return unloadedClassCount;
    }

    public void setUnloadedClassCount(long unloadedClassCount) {
        this.unloadedClassCount = unloadedClassCount;
    }

    @Override
    public void write(BufferWrapper buf) throws IOException {
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
        buf.writeString(name);
        buf.writeString(vmVersion);
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
    public void read(BufferWrapper buf) throws IOException {
        committedVirtualMemorySize = buf.readLong();
        freePhysicalMemorySize = buf.readLong();
        freeSwapSpaceSize = buf.readLong();
        processCpuLoad = buf.readDouble();
        processCpuTime = buf.readLong();
        systemCpuLoad = buf.readDouble();
        totalPhysicalMemorySize = buf.readLong();
        totalSwapSpaceSize = buf.readLong();

        name = buf.readString();
        vmVersion = buf.readString();
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
    }

    private void writeMemoryUsage(BufferWrapper buf, MemoryUsage memoryUsage) throws IOException {
        buf.writeLong(memoryUsage.getInit());
        buf.writeLong(memoryUsage.getUsed());
        buf.writeLong(memoryUsage.getCommitted());
        buf.writeLong(memoryUsage.getMax());
    }

    private MemoryUsage readMemoryUsage(BufferWrapper buf) throws IOException {
        long init = buf.readLong();
        long used = buf.readLong();
        long committed = buf.readLong();
        long max = buf.readLong();
        return new MemoryUsage(init, used, committed, max);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
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
