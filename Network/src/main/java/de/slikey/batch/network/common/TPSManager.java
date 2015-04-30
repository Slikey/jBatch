package de.slikey.batch.network.common;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Kevin Carstens
 * @since 30.04.2015
 */
public class TPSManager {

    public static final long TRACKED_SIZE = 30;

    private final Map<TickingManager, List<Long>> tracked;

    public TPSManager() {
        tracked = new HashMap<>();
    }

    public synchronized void reportTime(TickingManager tickingManager, long currentTime) {
        List<Long> entries = this.ensureManagerIsTracked(tickingManager);
        { // Prevent memory leak
            if (entries.size() >= TRACKED_SIZE)
                entries.remove(0);
        }
        { // Actually track
            entries.add(currentTime);
        }
    }

    public synchronized float getTPS(TickingManager tickingManager) {
        List<Long> entries = this.ensureManagerIsTracked(tickingManager);
        int size = entries.size();
        if (size < 2) {
            return 0f;
        }
        long first = entries.get(0);
        long last = entries.get(size - 1);
        long timeRange = last - first;
        return (float) timeRange / size;
    }

    private List<Long> ensureManagerIsTracked(TickingManager tickingManager) {
        List<Long> entries = tracked.get(tickingManager);
        if (entries == null) {
            tracked.put(tickingManager, entries = new LinkedList<>());
        }
        return entries;
    }

}
