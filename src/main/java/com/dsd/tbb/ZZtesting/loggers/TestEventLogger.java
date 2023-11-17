package com.dsd.tbb.ZZtesting.loggers;

import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.managers.FileAndDirectoryManager;
import com.dsd.tbb.util.TBBLogger;
import com.dsd.tbb.util.TimeUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TestEventLogger {
    private static final List<String> eventLog = new ArrayList<>();
    private static final String HEADER = "Timestamp,UUID,EventType,Entity ID,AdditionalInfo";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd-HH:mm:ss.SSSS");

    static {
        // Add column headings as the first line
        eventLog.add(HEADER);
    }

    /**
     * Records an event to the log.
     *
     * @param uuid          The UUID associated with the event.
     * @param eventType     The type of event.
     * @param additionalInfo Additional information related to the event.
     */
    public static synchronized void logEvent(String uuid, String eventType,String entityID, String additionalInfo) {
        if(!TrialsByBaby.MOD_IS_IN_TESTING) return;
        String dataLine = String.join(",",
                TimeUtil.getCurrentTimestamp(),
                uuid,
                eventType,
                entityID,
                additionalInfo
        );
        eventLog.add(dataLine);
    }

    public static void writeToFile() {
        if(!TrialsByBaby.MOD_IS_IN_TESTING) return;
        DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = fileNameFormatter.format(LocalDateTime.now())+"-TestEvents.csv";
        Path logDir = FileAndDirectoryManager.getLogDirectory(); // Make sure to define this method or variable
        Path filePath = Paths.get(logDir.toString(), fileName);

        try {
            Files.write(filePath, eventLog);
            TBBLogger.getInstance().info("Write Event Data", "Event data written to " + filePath);
            eventLog.clear();
            eventLog.add(HEADER);
        } catch (IOException e) {
            TBBLogger.getInstance().error("Write Event Data", "Error writing event data to file: " + e.getMessage());
        }
    }
}
