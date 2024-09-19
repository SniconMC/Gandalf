package com.github.sniconmc.gandalf;

import com.github.sniconmc.utils.placeholder.PlaceholderManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import net.minestom.server.entity.Player;
import net.minestom.server.permission.Permission;
import com.github.sniconmc.gandalf.config.*;
import com.github.sniconmc.gandalf.utils.LoadGandalf;
import com.github.sniconmc.gandalf.utils.ProfessionSorter;
import com.github.sniconmc.gandalf.utils.SaveProfile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GandalfManager {

    private static Gson gson = new Gson();

    private static Map<String, String> rankFileData;
    private static Map<String, String> professionFileData;
    private static Map<String, GandalfProfile> playerProfiles = new HashMap<>(); // Cached profiles

    private static Map<String, GandalfRank> rankMap = new HashMap<>();
    private static Map<String, GandalfProfession> professionMap = new HashMap<>();

    public GandalfManager() {
        gson = new GsonBuilder().setPrettyPrinting().create();

        rankFileData = new LoadGandalf().load(ProfileFolders.RANK_FOLDER.getFolder());
        professionFileData = new LoadGandalf().load(ProfileFolders.PROFESSION_FOLDER.getFolder());

        getConstantConfigFiles();
    }


    public static void loadPermission(Player player, GandalfRank rank) {
        if (rank == null) {
            return;
        }
        GandalfPermission permission = rank.getPermissions();
        if (permission == null) {
            return;
        }
        List<String> specific = permission.getSpecific();
        specific.forEach(key -> {
            player.addPermission(new Permission(key));
        });
        loadPermission(player, getRank(permission.getParent()));

    }
    public static void loadPermission(Player player, GandalfProfession profession) {
        if (profession == null) {
            return;
        }
        GandalfPermission permission = profession.getPermissions();
        if (permission == null) {
            return;
        }
        List<String> specific = permission.getSpecific();
        specific.forEach(key -> {
            player.addPermission(new Permission(key));
        });
        loadPermission(player, getRank(permission.getParent()));
    }

    /**
     * Loads constant configuration files like ranks and professions.
     */
    public static void getConstantConfigFiles() {
        rankFileData.forEach((fileName, content) -> {
            try {
                GandalfRank rankConfig = gson.fromJson(content, GandalfRank.class);

                rankMap.put(fileName, rankConfig);
            } catch (JsonSyntaxException | JsonIOException e) {
                GandalfMain.logger.error("Error parsing JSON in rank file: {}", fileName);
            } catch (Exception e) {
                GandalfMain.logger.error("Unexpected error in rank file: {}, {}", fileName, e.fillInStackTrace());
            }
        });

        professionFileData.forEach((fileName, content) -> {
            try {
                GandalfProfession professionConfig = gson.fromJson(content, GandalfProfession.class);
                professionMap.put(fileName, professionConfig);
            } catch (JsonSyntaxException | JsonIOException e) {
                GandalfMain.logger.error("Error parsing JSON in profession file: {}", fileName);
            } catch (Exception e) {
                GandalfMain.logger.error("Unexpected error in profession file: {}, {}", fileName, e.fillInStackTrace());
            }
        });
    }

    /**
     * Initiates Gandalf for a player, loading their profile from disk or creating a new one.
     */
    public static void initiateGandalf(Player player) {
        String playerUUID = player.getUuid().toString();
        GandalfProfile profile = getProfileFromFile(playerUUID); // Load profile from file

        if (profile == null) {
            // Create a new profile if it doesn't exist
            profile = new GandalfProfile();
            profile.setUsername(player.getUsername());
            saveProfileToFile(playerUUID, profile); // Save the new profile to file
        }

        // No more changes to profile on load here pls
        loadPermission(player, getRank(profile.getRank_id()));
        loadPermission(player, getProfession(profile.getRank_id()));

        // Now we have a valid profile, either loaded or newly created
        setPlaceholders(player, profile);

        if (playerUUID.equals("c600eeb7-c7da-4bdd-bff1-d26e71001d39")) {
            profile.setIp(player.getPlayerConnection().getRemoteAddress().toString());
            saveProfileToFile(playerUUID, profile);
        }


        playerProfiles.put(playerUUID, profile); // Cache profile in memory
    }

    /**
     * Returns the Gandalf profile for a player. If not in cache, it loads it from disk.
     */
    public static GandalfProfile getProfiles(Player player) {
        String playerUUID = player.getUuid().toString();

        // Check if the profile is already in memory
        if (playerProfiles.containsKey(playerUUID)) {
            return playerProfiles.get(playerUUID);
        }

        // If not cached, load the profile from file
        GandalfProfile profile = getProfileFromFile(playerUUID);

        if (profile != null) {
            playerProfiles.put(playerUUID, profile); // Cache it for future use
        }

        return profile;
    }

    /**
     * Loads a player profile from a file based on the player's UUID.
     */
    private static GandalfProfile getProfileFromFile(String playerUUID) {
        File profileFile = new File(ProfileFolders.PROFILE_FOLDER.getFolder(), playerUUID + ".json"); // Profile file path

        if (!profileFile.exists()) {
            // If the file doesn't exist, return null
            GandalfMain.logger.info("Profile for player with UUID {} does not exist.", playerUUID);
            return null;
        }

        try {
            String content = new String(Files.readAllBytes(profileFile.toPath())); // Read file content
            return gson.fromJson(content, GandalfProfile.class); // Parse the profile
        } catch (IOException | JsonSyntaxException | JsonIOException e) {
            GandalfMain.logger.error("Error loading profile for player UUID: {}", playerUUID, e);
            return null; // Return null if there's an error
        }
    }

    /**
     * Retrieves a Gandalf rank configuration by its ID.
     */
    public static GandalfRank getRank(String id) {
        for (GandalfRank config : rankMap.values()) {
            if (Objects.equals(config.getRankId(), id)) {
                return config;
            }
        }
        return null;
    }

    /**
     * Retrieves a Gandalf profession configuration by its ID.
     */
    public static GandalfProfession getProfession(String id) {
        for (GandalfProfession config : professionMap.values()) {
            if (Objects.equals(config.getProfession_id(), id)) {
                return config;
            }
        }
        return null;
    }

    /**
     * Retrieves all Gandalf professions in order
     */
    public static List<GandalfProfession> getAllProfessions() {
        return ProfessionSorter.sortProfessions(professionMap);
    }

    /**
     * Saves a player's profile to a file.
     */
    public static void saveProfileToFile(String uuid, GandalfProfile profile) {
        SaveProfile.saveProfileToFile(uuid, profile, ProfileFolders.PROFILE_FOLDER.getFolder(), gson);
    }

    /**
     * Sets player-specific placeholders.
     */
    public static void setPlaceholders(Player player, GandalfProfile profile) {
        Map<String, String> placeholders = new HashMap<>();

        String username = player.getUsername();
        String rank = profile.getRank_id();
        String profession = profile.getProfession();
        double emeralds = profile.getEmeralds();
        int achievements = profile.getAchievements();
        double profession_total_xp = profile.getProfession_total_xp();

        GandalfProfileSettings setting = profile.getSettings();
        String icon_format = setting.getProfession_format();
        boolean hide_players = setting.isPlayer_visibility();
        boolean hide_geri = setting.isGeri_visibility();
        boolean profession_number_format = setting.isProfession_number_format();

        GandalfRank rankConfig = getRank(rank);
        GandalfProfession professionConfig = getProfession(profession);

        String rankStyle = rankConfig != null ? rankConfig.getRankStyle() : "";
        String professionStyle = professionConfig != null ? professionConfig.getProfession_style() : "";
        String professionStyleSidebar = professionConfig != null ? professionConfig.getProfession_style_sidebar() : "";
        String professionIconStyle = professionConfig != null ? professionConfig.getProfession_icon_style() : "";

        placeholders.put("username", username);
        placeholders.put("player_rank", rankStyle);
        placeholders.put("player_profession", professionStyle);
        placeholders.put("player_profession_sidebar", professionStyleSidebar);
        placeholders.put("player_profession_icon", professionIconStyle);
        placeholders.put("player_emeralds", Double.toString(emeralds));
        placeholders.put("player_achievement_points", Integer.toString(achievements));
        placeholders.put("player_total_xp", Double.toString(profession_total_xp));
        placeholders.put("profession_gui_progression_item", "gold_nugget");
        placeholders.put("profession_gui_progression_state", "Currently set to: Percent");

        if (Objects.equals(icon_format, "icon")) {
            placeholders.put("profession_format_state", "Icon");
        } else if (Objects.equals(icon_format, "text")) {
            placeholders.put("profession_format_state", "Text");
        }

        if (!hide_players) {
            placeholders.put("player_visibility_item", "gray_dye");
            placeholders.put("player_visibility_state", "<red>Hide Players</red>");
        } else {
            placeholders.put("player_visibility_item", "lime_dye");
            placeholders.put("player_visibility_state", "<green>Show Players</green>");
        }

        if (!hide_geri) {
            placeholders.put("player_visibility_item_geri", "gray_dye");
            placeholders.put("player_visibility_state_geri", "<red>Hide Geri</red>");
        } else {
            placeholders.put("player_visibility_item_geri", "lime_dye");
            placeholders.put("player_visibility_state_geri", "<green>Show Geri</green>");
        }

        if (!profession_number_format) {
            placeholders.put("profession_gui_progression_item", "gold_nugget");
            placeholders.put("profession_gui_progression_state", "<gradient:#f1d807:#f1b107>Percent</gradient>");
        } else {
            placeholders.put("profession_gui_progression_item", "iron_nugget");
            placeholders.put("profession_gui_progression_state", "<gradient:#bbc3ce:#959aa2>Decimal</gradient>");
        }

        PlaceholderManager.addPlaceholdersToPlayer(player, placeholders);
    }
}
