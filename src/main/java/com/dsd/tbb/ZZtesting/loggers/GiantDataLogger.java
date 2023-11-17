package com.dsd.tbb.ZZtesting.loggers;

import com.dsd.tbb.main.TrialsByBaby;
import com.dsd.tbb.managers.FileAndDirectoryManager;
import com.dsd.tbb.util.EnumTypes;
import com.dsd.tbb.util.TBBLogger;
import com.dsd.tbb.util.TimeUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GiantDataLogger {
    private List<String> testData = new ArrayList<>();
    private static final String HEADER = "Timestamp,UUID,Entity ID,Name,Event,Client or Server Entry,State,Has a Target," +
            "Location,Motion,Active Goal,Health Percentage,Active Animation";
    private int entityID = 0;
    private String uuid = "0";
    private final boolean isClientLog;

    public GiantDataLogger(int entityID, String uuid, boolean isClientSide){
        this.entityID = entityID;
        this.uuid = uuid;
        this.isClientLog = isClientSide;
        testData.add(HEADER);
    }
    public synchronized void recordData(String name, String event,boolean isClient, EnumTypes.GiantState state,
                                        boolean hasTarget, BlockPos pos, Vec3 motion, String activeGoal,
                                        float healthPercentage,String activeAnim){ //add - animation stuff
        if(!TrialsByBaby.MOD_IS_IN_TESTING) return;
        String timestamp = TimeUtil.getCurrentTimestamp();
        String locString = String.format("X[%d] Y[%d] Z[%d]",pos.getX() ,pos.getY(),pos.getZ());
        String velString = String.format("X[%f] Y[%f] Z[%f]",motion.x,motion.y,motion.z);
        String dataLine = String.join(",",
                timestamp,
                uuid,
                String.valueOf(entityID),
                name,
                event,
                isClient? "Client" : "Server",
                state.name(),
                hasTarget? "true" : "false",
                locString,
                velString,
                activeGoal,
                String.format("%.2f",healthPercentage*100),
                activeAnim);

        testData.add(dataLine);
    }

    public void writeToFile() {
        if(!TrialsByBaby.MOD_IS_IN_TESTING) return;
        if(!isClientLog) {
            DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String fileName = fileNameFormatter.format(LocalDateTime.now()) + "-" + this.entityID +
                    (isClientLog ? "Client" : "Server") + "-GiantData.csv";
            Path logDir = FileAndDirectoryManager.getLogDirectory();
            Path filePath = Paths.get(logDir.toString(), fileName);

            try {
                Files.write(filePath, testData);
                //TBBLogger.getInstance().info("write Test Data", "Test data written to " + filePath);
                testData.clear();
                testData.add(HEADER);

            } catch (IOException e) {
                TBBLogger.getInstance().error("write Test Data", "Error writing test data to file: " + e.getMessage());
            }
        }
    }
}
