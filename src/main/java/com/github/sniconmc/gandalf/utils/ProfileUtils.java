package com.github.sniconmc.gandalf.utils;

import com.github.sniconmc.sidebar.SidebarManager;
import net.minestom.server.entity.Player;
import com.github.sniconmc.gandalf.GandalfManager;
import com.github.sniconmc.gandalf.config.GandalfProfile;

public class ProfileUtils {

    public static void update(Player player, GandalfProfile profile) {
        GandalfManager.saveProfileToFile(player.getUuid().toString(), profile); // Save profile
        SidebarManager.reloadSidebars(); // Reload the sidebar
    }

    public static void addEmeralds(Player player, double count) {
        GandalfProfile profile = GandalfManager.getProfiles(player);
        profile.addEmeralds(count);
        update(player, profile);
    }

    public static void removeEmeralds(Player player, double count) {
        GandalfProfile profile = GandalfManager.getProfiles(player);
        profile.removeEmeralds(count);
        update(player, profile);
    }

    public static void setEmeralds(Player player, double count) {
        GandalfProfile profile = GandalfManager.getProfiles(player);
        profile.setEmeralds(count);
        update(player, profile);
    }

    public static void addExperience(Player player, double count) {
        GandalfProfile profile = GandalfManager.getProfiles(player);
        profile.addProfession_total_xp(count);
        CalculateProfession.updateProfession(player);
        update(player, profile);
    }

    public static void removeExperience(Player player, double count) {
        GandalfProfile profile = GandalfManager.getProfiles(player);
        profile.removeProfession_total_xp(count);
        CalculateProfession.updateProfession(player);
        update(player, profile);
    }

    public static void setExperience(Player player, double count) {
        GandalfProfile profile = GandalfManager.getProfiles(player);
        profile.setProfession_total_xp(count);
        CalculateProfession.updateProfession(player);
        update(player, profile);
    }
}
