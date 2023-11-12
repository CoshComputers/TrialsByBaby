package com.dsd.tbb.ZZtesting.loggers;

import com.dsd.tbb.managers.FileAndDirectoryManager;
import com.dsd.tbb.util.TBBLogger;
import com.dsd.tbb.util.TimeUtil;
import net.minecraftforge.event.TickEvent;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ServerPerformanceMonitor {
    private static final int SAMPLE_INTERVAL = 20;
    private static final String HEADER = "Timestamp,Time Taken (ms),TPS,Total Memory (bytes),Free Memory (bytes),Used Memory (bytes),Garbage Collection Count,Thread Count";
    private static long lastSampleTime = System.currentTimeMillis();
    private static int tickCount = 0;
    private static List<String> performanceData = new ArrayList<>();
    private static long previousGarbageCollectionCount = 0;


    static {
        // Add column headings as the first line
        performanceData.add(HEADER);
    }


    public synchronized static void onTick(TickEvent.ServerTickEvent event) {
        tickCount++;
        if (tickCount >= SAMPLE_INTERVAL) {
            long currentTime = System.currentTimeMillis();
            long timeTaken = currentTime - lastSampleTime;
            double tps = 1000.0 / (timeTaken / (double) SAMPLE_INTERVAL);

            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long usedMemory = totalMemory - freeMemory;

            // Get Thread Count
            int threadCount = Thread.activeCount();


            int playerCount = event.getServer().getPlayerCount();
            float averageTickTime = event.getServer().getAverageTickTime();



            // Placeholder for Garbage Collection Count
            // Since directly accessing GarbageCollectorMXBean might not be feasible, you need a custom implementation
            long gcCount = getGarbageCollectionCount(); // Implement this method based on your mod's capability

            // Format data with 2 decimal places for TPS
            String dataLine = String.join(",",
                    TimeUtil.getCurrentTimestamp(),
                    String.format("%d", timeTaken),
                    String.format("%.2f", tps),
                    String.format("%d", totalMemory),
                    String.format("%d", freeMemory),
                    String.format("%d", usedMemory),
                    String.format("%d", gcCount),
                    String.format("%d", threadCount)
            );
            performanceData.add(dataLine);

            // Reset for the next sample
            lastSampleTime = currentTime;
            tickCount = 0;
        }
    }

    private static long getGarbageCollectionCount() {
        long totalGarbageCollections = 0;

        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcBean : gcBeans) {
            long count = gcBean.getCollectionCount();
            if (count != -1) {  // -1 if the collection count is undefined for this collector.
                totalGarbageCollections += count;
            }
        }

        long collectionsSinceLastCycle = totalGarbageCollections - previousGarbageCollectionCount;
        previousGarbageCollectionCount = totalGarbageCollections; // Update for the next cycle

        return collectionsSinceLastCycle;
    }


    public static void writeToFile() {

        DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        String fileName = fileNameFormatter.format(LocalDateTime.now())+ "-ServerPerformance.csv";
        Path logDir = FileAndDirectoryManager.getLogDirectory();
        Path filePath = Paths.get(logDir.toString(), fileName);;

        try {
            Files.write(filePath, performanceData);
            TBBLogger.getInstance().info("write Performance","Performance data written to " + filePath);
            performanceData.clear();
            performanceData.add(HEADER);
        } catch (IOException e) {
            TBBLogger.getInstance().error("write Performance","Error writing performance data to file: " + e.getMessage());
        }
    }
}

