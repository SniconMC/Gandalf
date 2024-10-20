package com.github.sniconmc.gandalf.utils;

import com.github.sniconmc.gandalf.GandalfMain;
import com.github.sniconmc.gandalf.database.DatabasePlayer;
import com.github.sniconmc.sidebar.SidebarManager;
import net.minestom.server.entity.Player;
import com.github.sniconmc.gandalf.GandalfManager;
import com.github.sniconmc.gandalf.config.GandalfProfile;

public class ProfileUtils {

    public static void update(DatabasePlayer dataPlayer) {
        GandalfMain.dbManager.insertPlayer(dataPlayer);
        SidebarManager.reloadSidebars(); // Reload the sidebar
    }

    public static void addEmeralds(Player player, double count) {
        DatabasePlayer dataPlayer = GandalfManager.getDataPlayer(player);
        dataPlayer.setEmeralds(dataPlayer.getEmeralds() + count);
        update(dataPlayer);
    }

    public static void removeEmeralds(Player player, double count) {

        DatabasePlayer dataPlayer = GandalfManager.getDataPlayer(player);
        dataPlayer.setEmeralds(dataPlayer.getEmeralds() - count);
        update(dataPlayer);
    }

    public static void setEmeralds(Player player, double count) {
        DatabasePlayer dataPlayer = GandalfManager.getDataPlayer(player);
        dataPlayer.setEmeralds(count);
        update(dataPlayer);
    }

    public static void addExperience(Player player, double count) {
        /*GandalfProfile profile = GandalfManager.getProfiles(player);*/
        DatabasePlayer dataPlayer = GandalfManager.getDataPlayer(player);
        dataPlayer.setProfessionTotalXP(dataPlayer.getProfessionTotalXP() + count);
        update(dataPlayer);
    }

    public static void removeExperience(Player player, double count) {

        DatabasePlayer dataPlayer = GandalfManager.getDataPlayer(player);
        dataPlayer.setProfessionTotalXP(dataPlayer.getProfessionTotalXP() - count);
        update(dataPlayer);
    }

    public static void setExperience(Player player, double count) {
        DatabasePlayer dataPlayer = GandalfManager.getDataPlayer(player);
        dataPlayer.setProfessionTotalXP(count);
        update(dataPlayer);
    }
}
