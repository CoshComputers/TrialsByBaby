package com.dsd.tbb.util;


import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.managers.FileAndDirectoryManager;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TBBLogger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[94m";
    private List<TimerLogEntry> timerLogList = new ArrayList<>();
    private List<String> bulkLogList = new ArrayList<>();
    private static final TBBLogger INSTANCE = new TBBLogger();
    private final Logger logger;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSSS");


    private TBBLogger() {
        this.logger = LogUtils.getLogger();

    }

    public static TBBLogger getInstance() {
        return INSTANCE;
    }

    public void debug(String method, String message) {
        bulkLog("[DEBUG]"+method,message);
       if(ConfigManager.getInstance().getTrialsConfig().isDebugOn()) {
            logger.debug(ANSI_BLUE + "[" + method + "]" +message + ANSI_RESET);
       }

    }
    // Similarly for other log levels...
    public void info(String method, String message) {
        bulkLog("[INFO]"+method,message);
        logger.info(ANSI_GREEN + "[" + method + "]" + message + ANSI_RESET);
    }
    public void error(String method, String message) {
        bulkLog("[ERROR]"+method,message);
        logger.error(ANSI_RED + "[" + method + "]" +message + ANSI_RESET);
    }
    public void warn(String method, String message) {
        bulkLog("[WARN]"+method,message);
        logger.warn(ANSI_YELLOW + "[" + method + "]" +message + ANSI_RESET);}

    public void broadcastMessage(String message) {

    }


    public void sendPlayerMessage(Player player, String message) {

    }

    public void bulkLog(String methodName, String message){
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(formatCurrentTime()).append("],");
        sb.append("[").append(methodName).append("],").append(message);
        if(bulkLogList.size() > 5000){
            writeLogToFile();
            bulkLogList.clear();
        }
        this.bulkLogList.add(sb.toString());
    }
    public void preCall(String methodName) {
        // Store the method name and current time
        TimerLogEntry entry = new TimerLogEntry();
        entry.timeStampString = formatCurrentTime();
        entry.methodName = methodName;
        entry.startTime = System.nanoTime();
        timerLogList.add(entry);
    }
    public void postCall() {
        // Find the last log entry and update its end time
        if(!timerLogList.isEmpty()) {
            TimerLogEntry lastEntry = timerLogList.get(timerLogList.size() - 1);
            lastEntry.endTime = System.nanoTime();
        }else{
            logger.error("Attempting to store TimerLog in Post Call without PreCall first");
        }
    }

    public void outputBulkToConsole(){
        for (String l: bulkLogList) {
            logger.debug(l);
        }
        bulkLogList.clear();
    }

    public void writeLogToFile() {
        DateTimeFormatter fileNameFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

        String fileName = fileNameFormatter.format(LocalDateTime.now()) + ".txt";
        Path logFile = FileAndDirectoryManager.getLogDirectory().resolve(fileName);

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(logFile.toUri()), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            for(String entry : bulkLogList){

                writer.write(entry);
                writer.newLine();  // Write a new line character after each entry
            }
        } catch (IOException e) {
            e.printStackTrace();
            TBBLogger.getInstance().error("outputLogToFile","Failed to Store Log in File, outputting to Console if Debug On");
            if(ConfigManager.getInstance().getTrialsConfig().isDebugOn()) {
                TBBLogger.getInstance().outputBulkToConsole();
            }
            // Handle the exception appropriately, e.g., log it, rethrow it, etc.
        }
    }

    public void outputtimerLogToConsole(){
        for (TimerLogEntry t: timerLogList) {
            logger.debug(t.generateLog());
        }
        timerLogList.clear();
    }
    private static String formatCurrentTime() {
        LocalDateTime currentTime = LocalDateTime.now();
        return "[" + FORMATTER.format(currentTime) + "]";
    }

    //TODO: Create this entry - to output the player details and potential broadcast a message
    public void playerLogEntry(Player player) {
    }

    private static class TimerLogEntry {
        String methodName;
        String timeStampString;
        long startTime;
        long endTime;

        public String generateLog(){
            StringBuilder sb = new StringBuilder();
            sb.append("[").append(this.timeStampString).append("]");
            sb.append("Method [").append(this.methodName).append("]\t");
            sb.append("Elapsed Time [").append(this.endTime - this.startTime).append("] Nano Seconds");
            return sb.toString();
        }
    }
}
