package com.github.sniconmc.gandalf;

import net.minestom.server.entity.Player;
import com.github.sniconmc.gandalf.config.GandalfProfession;
import com.github.sniconmc.gandalf.config.GandalfProfile;
import com.github.sniconmc.gandalf.config.GandalfRank;

public class ProfilePlayer extends Player {

    private GandalfProfile profile;
    private GandalfProfession profession;
    private GandalfRank rank;

    public ProfilePlayer(Player player, GandalfProfession profession, GandalfRank rank) {
        super(player.getUuid(), player.getUsername(), player.getPlayerConnection());
        this.profile = profile;
        this.profession = profession;
        this.rank = rank;
    }
}
