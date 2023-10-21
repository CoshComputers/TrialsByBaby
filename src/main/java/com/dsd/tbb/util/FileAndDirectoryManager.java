package com.dsd.tbb.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileAndDirectoryManager {

    private static Path serverRootDirectory;
    private static Path modDirectory;

    private static Path playerDataDirectory;

    private FileAndDirectoryManager() {
        // Private constructor to prevent instantiation
    }

    public static void initialize(Path rootPath) {
        serverRootDirectory = rootPath;
        modDirectory = rootPath.resolve("trialsbybaby");
        playerDataDirectory = modDirectory.resolve("playerdata");
        // Ensure mod directory exists
        try {
            createDirectory(modDirectory);
            createDirectory(playerDataDirectory);
        } catch (IOException e) {
            CustomLogger.getInstance().error("Failed to create Directories for Mod.");
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

    /********************************* LOG HELPERS *****************************************/
    public void logFileContents(Path filePath) {
        try {
            String content = new String(Files.readAllBytes(filePath));
            CustomLogger.getInstance().info(String.format("File content: %s", content));
        } catch (IOException e) {
            CustomLogger.getInstance().error(String.format("Failed to read file: %s\n%s", filePath, e));
        }
    }
}
