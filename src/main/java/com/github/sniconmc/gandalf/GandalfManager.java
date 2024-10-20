package com.github.sniconmc.gandalf;

import com.github.sniconmc.gandalf.database.DatabasePlayer;
import com.github.sniconmc.gandalf.utils.CalculateProfession;
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

        CalculateProfession.updateProfession(player);

        DatabasePlayer dataPlayer = getDataPlayer(player);

        // No more changes to profile on load here pls
        loadPermission(player, getRank(dataPlayer.getRankId()));
        loadPermission(player, getProfession(dataPlayer.getProfession()));



        // Now we have a valid profile, either loaded or newly created
        setPlaceholders(player, dataPlayer);
    }

    /**
     * Returns the Gandalf profile for a player. If not in cache, it loads it from disk.
     */
    public static DatabasePlayer getDataPlayer(Player player) {
        String playerUUID = player.getUuid().toString();

        // If not cached, load the dataPlayer from the database
        DatabasePlayer dataPlayer = GandalfMain.dbManager.getPlayer(playerUUID);

        if (dataPlayer == null) {
            dataPlayer = new DatabasePlayer(playerUUID, player.getUsername()); // Create new database player with default stats
            GandalfMain.dbManager.insertPlayer(dataPlayer); // add to database
        }

        return dataPlayer;
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
     * Sets player-specific placeholders.
     */
    public static void setPlaceholders(Player player, DatabasePlayer dataPlayer) {
        Map<String, String> placeholders = new HashMap<>();

        String username = player.getUsername();
        String rank = dataPlayer.getRankId();
        String profession = dataPlayer.getProfession();
        double emeralds = dataPlayer.getEmeralds();
        int achievements = dataPlayer.getAchievements();
        double profession_total_xp = dataPlayer.getProfessionTotalXP();


        String icon_format = dataPlayer.getProfessionFormat();
        boolean hide_players = dataPlayer.isPlayerVisibility();
        boolean hide_geri = dataPlayer.isGeriVisibility();
        boolean profession_number_format = dataPlayer.isProfessionNumberFormat();

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
