package com.dsd.tbb.managers;

import com.dsd.tbb.util.TBBLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileAndDirectoryManager {

    private static Path serverRootDirectory;
    private static Path modDirectory;
    private static Path worldDirectory;
    private static Path playerDataDirectory;
    private static Path logDirectory;

    private FileAndDirectoryManager() {
        // Private constructor to prevent instantiation
    }

    public static void initialize(Path rootPath) {
        serverRootDirectory = rootPath;
        modDirectory = rootPath.resolve("trialsbybaby");
        playerDataDirectory = modDirectory.resolve("playerdata");
        logDirectory = modDirectory.resolve("log");
        worldDirectory = modDirectory.resolve("worlddata");
        // Ensure mod directory exists
        try {
            createDirectory(modDirectory);
            createDirectory(playerDataDirectory);
            createDirectory(logDirectory);
            createDirectory(worldDirectory);
        } catch (IOException e) {
            TBBLogger.getInstance().error("initialize (FDM)","Failed to create Directories for Mod.");
            e.printStackTrace();  // Handle exceptions as appropriate for your use case
        }
    }

    // ... other existing methods ...

    public static Path getServerRootDirectory() {
        return serverRootDirectory;
    }

    public static Path getPlayerDataDirectory() { return playerDataDirectory;}

    public static Path getModDirectory() {
        return modDirectory;
    }
    public static Path getWorldDirectory() { return  worldDirectory; }

    public static void createDirectory(Path dirPath) throws IOException {
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }
    }

    public static boolean fileExists(Path filePath) {
        return Files.exists(filePath);
    }

    public static void copyFileFromResources(String subdirectory, String fileName, Path targetPath) throws IOException {
        try (InputStream resourceInputStream = FileAndDirectoryManager.class.getResourceAsStream("/" + subdirectory + "/" + fileName)) {
            if (resourceInputStream == null) {
                throw new IOException("Resource not found: " + subdirectory + "/" + fileName);
            }
            Files.copy(resourceInputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public static Path getLogDirectory() {
        return logDirectory;
    }

    /********************************* LOG HELPERS *****************************************/
    public void logFileContents(Path filePath) {
        try {
            String content = new String(Files.readAllBytes(filePath));
            TBBLogger.getInstance().debug("logFileContents",String.format("File content: %s", content));
        } catch (IOException e) {
            TBBLogger.getInstance().error("logFileContents",String.format("Failed to read file: %s\n%s", filePath, e));
        }
    }
}
