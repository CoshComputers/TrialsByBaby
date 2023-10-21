package com.dsd.tbb.util;


import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CustomLogger {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    private List<TimerLogEntry> timerLogList = new ArrayList<>();
    private List<String> bulkLogList = new ArrayList<>();
    private static final CustomLogger INSTANCE = new CustomLogger();
    private final Logger logger;
    private boolean isDebugOn;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private CustomLogger() {
        this.logger = LogUtils.getLogger();
        this.isDebugOn = false; //assuming no debug until config is loaded.
    }

    public static CustomLogger getInstance() {
        return INSTANCE;
    }
    public void setDebugOn(boolean debugOn){
        this.isDebugOn = debugOn;
    }

    public void debug(String message) {
       // if(this.isDebugOn) {
            logger.debug(ANSI_BLUE + message);
       // }

    }
    // Similarly for other log levels...
    public void info(String message) {
        logger.info(ANSI_GREEN + message);
    }
    public void error(String message) {
        logger.error(ANSI_RED + message);
    }
    public void warn(String message) { logger.warn(ANSI_YELLOW + message);}

    public void broadcastMessage(String message) {

    }


    public void sendPlayerMessage(Player player, String message) {

    }

    public void bulkLog(String methodName, String message){
        StringBuilder sb = new StringBuilder();
        sb.append(ANSI_YELLOW).append("[").append(formatCurrentTime()).append("]");
        sb.append("[").append(methodName).append("]").append(message);
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
    public void outputtimerLogToConsole(){
        for (TimerLogEntry t: timerLogList) {
            logger.debug(t.generateLog());
        }
        timerLogList.clear();
    }
    private static String formatCurrentTime() {
        LocalTime currentTime = LocalTime.now();
        return "[" + FORMATTER.format(currentTime) + "]";
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
