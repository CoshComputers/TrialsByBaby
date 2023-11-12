package com.dsd.tbb.ZZtesting.loggers;

import com.dsd.tbb.managers.FileAndDirectoryManager;
import com.dsd.tbb.util.TBBLogger;
import com.dsd.tbb.util.TimeUtil;
import net.minecraft.core.BlockPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TestResultData {
    private static List<String> testData = new ArrayList<>();
    private static final String HEADER = "Timestamp,UUID,Location,Baby Zombie Count,Giant Count,Boss Bar Count";

    static {
        // Add column headings as the first line
        testData.add(HEADER);
    }

    public synchronized static void recordData(String uuid, BlockPos pos,int babyCount,int giantCount,int bossBarCount) {
        String timestamp = TimeUtil.getCurrentTimestamp();
        String locString = String.format("X[%d] Y[%d] Z[%d]",pos.getX(),pos.getY(),pos.getZ());
        String dataLine = String.join(",",
                timestamp,
                uuid,
                locString,
                String.valueOf(babyCount),
                String.valueOf(giantCount),
                String.valueOf(bossBarCount)
        );
        testData.add(dataLine);
    }

    public static void writeToFile() {
        DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String fileName = fileNameFormatter.format(LocalDateTime.now()) + "-TestData.csv";
        Path logDir = FileAndDirectoryManager.getLogDirectory();
        Path filePath = Paths.get(logDir.toString(), fileName);

        try {
            Files.write(filePath, testData);
            TBBLogger.getInstance().info("write Test Data", "Test data written to " + filePath);
            testData.clear();
            testData.add(HEADER);

        } catch (IOException e) {
            TBBLogger.getInstance().error("write Test Data", "Error writing test data to file: " + e.getMessage());
        }
    }
}
