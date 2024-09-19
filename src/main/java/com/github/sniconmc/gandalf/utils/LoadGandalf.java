package com.github.sniconmc.gandalf.utils;

import com.github.sniconmc.gandalf.GandalfMain;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The {@code LoadGandalf} class provides utility methods to load JSON files from a specified directory.
 * This class recursively searches for JSON files within the given directory and its subdirectories,
 * and processes each JSON file by reading its contents and storing it in a map with the filename (without extension)
 * as the key and the JSON content as the value.
 * <p>This class is intended to be used for loading configuration or data files stored in JSON format,
 * facilitating the initialization or reloading of data for applications.</p>
 */

public class LoadGandalf {

    /**
     * Loads JSON files from the specified directory. This method first ensures that the provided parent directory
     * exists and is indeed a directory. If the directory does not exist, it attempts to create it. Then, it
     * searches recursively for all JSON files within the directory and processes them by reading their contents
     * and storing them in a map.
     *
     * @param parentFolder The directory to search for JSON files.
     * @return A map where the keys are the filenames (without the .json extension) and the values are the contents
     * of the JSON files.
     */

    public Map<String, String> load(File parentFolder) {

        if (!parentFolder.exists() || !parentFolder.isDirectory()) {
            GandalfMain.logger.warn("Parent folder does not exist or is not a directory: {}", parentFolder.getAbsolutePath());
            boolean hasCreated = parentFolder.mkdirs();

            if (hasCreated) {
                GandalfMain.logger.info("Created folder '{}'!", parentFolder.getName());
            } else {
                GandalfMain.logger.warn("Failed to create folder '{}'!", parentFolder.getName());
            }

        }

        // A set to store the names of all files processed during this load
        Set<File> foundJsonFiles = searchFiles(parentFolder);

        return processJsonFiles(foundJsonFiles, parentFolder);
    }

    /**
     * Recursively searches for JSON files within the given directory and its subdirectories.
     * If the directory is empty or does not contain any files, a warning is logged.
     *
     * @param folder The directory to search for JSON files.
     * @return A set of files that have the .json extension.
     */

    private Set<File> searchFiles(File folder){

        File[] files = folder.listFiles();

        if (files == null) {
            GandalfMain.logger.warn("The {} folder does not contain any files", folder.getName());
            return new HashSet<>();
        }

        Set<File> processedFiles = new HashSet<>();

        for (File file : files) {
            if (file.isDirectory()) {
                processedFiles.addAll(searchFiles(file));
            }
            if (file.isFile() && file.getName().endsWith(".json")) {
                processedFiles.add(file);
            }

        }

        return processedFiles;

    }

    /**
     * Processes a set of JSON files by reading their contents and storing them in a map.
     * Each file's name (without the .json extension) is used as the key in the map, and the file's content
     * is the associated value. If an error occurs while reading or parsing a file, it is logged and the file
     * is skipped.
     *
     * @param files        A set of JSON files to be processed.
     * @param parentFolder The parent directory containing the files, used for logging purposes.
     * @return A map where the keys are the filenames (without the .json extension) and the values are the contents
     * of the JSON files.
     */

    private static Map<String, String> processJsonFiles(Set<File> files, File parentFolder) {

        if (files == null || files.isEmpty()) {
            GandalfMain.logger.warn("The {} folder does not contain any json files", parentFolder.getName());
            return new HashMap<>();
        }

        Map<String, String> processedFiles = new HashMap<>();

        for (File file : files) {
            try {
                String config = new String(Files.readAllBytes(file.toPath()));
                String fileName = file.getName().replace(".json", "");

                processedFiles.put(fileName, config);

            } catch (JsonSyntaxException | JsonIOException e) {
                // Handle Gson-specific errors
                GandalfMain.logger.error("Error parsing JSON file: {}", file.getName());
            } catch (IOException e) {
                // Handle IO errors
                GandalfMain.logger.error("Error loading file in folder '{}' file: {}", parentFolder.getName(), file.getName());
            }
        }

        return processedFiles;
    }

    /**
     * Searches for a specific JSON file by its name within the parent folder and its subdirectories.
     * If the file is found, its contents are returned as a string. If the file is not found, an empty
     * string is returned.
     *
     * @param parentFolder The directory where the search for the JSON file starts.
     * @param fileName     The name of the JSON file to search for (without the .json extension).
     * @return The content of the JSON file as a string if found, otherwise an empty string.
     */
    public String loadSpecificJsonFile(File parentFolder, String fileName) {

        // Recursively search for JSON files
        Set<File> foundJsonFiles = searchFiles(parentFolder);

        // Iterate through the found JSON files and search for the file that matches the fileName
        for (File file : foundJsonFiles) {
            if (file.getName().equalsIgnoreCase(fileName + ".json")) {
                try {
                    // Read the content of the file and return it as a string
                    return new String(Files.readAllBytes(file.toPath()));
                } catch (IOException e) {
                    GandalfMain.logger.error("Error loading specific file '{}' in folder '{}'", fileName, parentFolder.getName());
                    return "";
                }
            }
        }

        // If the file is not found, return an empty string
        GandalfMain.logger.warn("The file {}.json could not be found in folder: {}", fileName, parentFolder.getAbsolutePath());
        return "";
    }
}
