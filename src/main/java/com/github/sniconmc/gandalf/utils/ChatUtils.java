package com.github.sniconmc.gandalf.utils;

import com.github.sniconmc.gandalf.database.DatabasePlayer;
import com.github.sniconmc.utils.placeholder.PlaceholderReplacer;
import com.github.sniconmc.utils.text.ColorUtils;
import com.github.sniconmc.utils.text.TextUtils;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;
import com.github.sniconmc.gandalf.GandalfManager;
import com.github.sniconmc.gandalf.config.GandalfProfile;
import com.github.sniconmc.gandalf.config.GandalfProfileSettings;
import com.github.sniconmc.gandalf.config.GandalfRank;


public class ChatUtils {

    // TODO use Audiences

    public static void sendChatMessage(Player viewer, Player sender, PlayerChatEvent event) {

        DatabasePlayer dataPlayer = GandalfManager.getDataPlayer(viewer);
        if (dataPlayer == null) {
            return;
        }
        GandalfRank rank = GandalfManager.getRank(dataPlayer.getRankId());
        if (rank == null) {
            return;
        }

        String format = "";

        if (dataPlayer.getProfessionFormat().equals("icon")) {
            format = rank.getRankFormatSimple();
        }
        if (dataPlayer.getProfessionFormat().equals("text")) {
            format = rank.getRankFormat();
        }
        Component message = TextUtils.convertStringToComponent(PlaceholderReplacer.replacePlaceholders(sender, format)).append(Component.text(": " + event.getMessage()).color(ColorUtils.StringToTextColor(rank.getRankChatColor())));
        viewer.sendMessage(message);

    }
}
