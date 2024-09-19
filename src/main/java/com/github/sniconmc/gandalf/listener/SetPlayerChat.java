package com.github.sniconmc.gandalf.listener;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChatEvent;
import com.github.sniconmc.gandalf.utils.ChatUtils;

public class SetPlayerChat {

    public SetPlayerChat(){
        MinecraftServer.getGlobalEventHandler().addListener(PlayerChatEvent.class, event -> {
            event.setCancelled(true);
            Player sender = event.getPlayer();

            for (Player viewer : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
                ChatUtils.sendChatMessage(viewer, sender, event);
            }

        });
    }
}
