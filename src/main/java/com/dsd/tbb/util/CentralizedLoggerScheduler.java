package com.dsd.tbb.util;

import com.dsd.tbb.ZZtesting.loggers.ServerPerformanceMonitor;
import com.dsd.tbb.ZZtesting.loggers.TestEventLogger;
import com.dsd.tbb.ZZtesting.loggers.TestResultData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class CentralizedLoggerScheduler {
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public CentralizedLoggerScheduler() {
        // Schedule periodic data writing
        executorService.scheduleAtFixedRate(ServerPerformanceMonitor::writeToFile, 0, 3, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(TestEventLogger::writeToFile, 0, 3, TimeUnit.MINUTES);
        executorService.scheduleAtFixedRate(TestResultData::writeToFile, 0, 3, TimeUnit.MINUTES);

        // Add shutdown hook within the constructor
        Runtime.getRuntime().addShutdownHook(new Thread(this::writeAllDataAndShutdown));
    }

    private void writeAllDataAndShutdown() {
        // Write data one last time
        ServerPerformanceMonitor.writeToFile();
        TestEventLogger.writeToFile();
        TestResultData.writeToFile();
        ModUtilities.writeGiantLogs();

        // Shutdown the executor service
        shutdownExecutorService();
    }

    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                // Handle failure to shut down gracefully
            }
        } catch (InterruptedException e) {
            // Handle interruption
        }
    }
}
