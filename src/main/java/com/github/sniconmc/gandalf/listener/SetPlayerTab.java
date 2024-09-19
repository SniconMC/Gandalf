package com.github.sniconmc.gandalf.listener;

import com.github.sniconmc.utils.text.TextUtils;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerSpawnEvent;
import com.github.sniconmc.gandalf.utils.TabUtils;


public class SetPlayerTab {

    public SetPlayerTab() {
        MinecraftServer.getGlobalEventHandler().addListener(PlayerSpawnEvent.class, event -> {
            MinecraftServer.getSchedulerManager().scheduleNextTick(() -> {
                Player joinedPlayer = event.getPlayer();

                Component header = TextUtils.convertStringToComponent("<gray>→</gray> <gradient:#ffff1c:gold><bold>play.znopp.pw</bold></gradient> <gray>←</gray>");
                Component footer = TextUtils.convertStringToComponent("<gray>»</gray> <gradient:gold:#ffff1c>store.z</gradient><gradient:#ffff1c:gold>nopp.pw</gradient> <gray>«</gray>");

                joinedPlayer.sendPlayerListHeaderAndFooter(header, footer);

                for (Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                    TabUtils.setExistingPlayer(onlinePlayer, joinedPlayer);
                }

                for (Player onlinePlayer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                    if (joinedPlayer == onlinePlayer) {
                        continue;
                    }
                    TabUtils.setExistingPlayer(joinedPlayer, onlinePlayer);
                }
            });
        });
    }
}

