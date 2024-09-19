package com.github.sniconmc.gandalf.utils;

import com.github.sniconmc.gandalf.GandalfMain;
import com.google.gson.Gson;
import com.github.sniconmc.gandalf.config.GandalfProfile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SaveProfile {

    public static void saveProfileToFile(String uuid, GandalfProfile profile, File dataProfileFolder, Gson gson) {
        // Ensure profile directory exists
        if (!dataProfileFolder.exists()) {
            dataProfileFolder.mkdirs();
        }

        // Create the file object with the UUID as the name
        File profileFile = new File(dataProfileFolder, uuid + ".json");

        try (FileWriter writer = new FileWriter(profileFile)) {
            // Serialize the GandalfProfile object to JSON and write it to the file
            gson.toJson(profile, writer);
        } catch (IOException e) {
            GandalfMain.logger.error("Failed to save profile for player with UUID {}: {}", uuid, e.getMessage());
        }
    }

}
