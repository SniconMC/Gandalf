package com.github.sniconmc.gandalf.config;

public class GandalfProfileSettings {

    private boolean player_visibility;
    private boolean geri_visibility;
    private String profession_format;
    private boolean profession_number_format;

    public GandalfProfileSettings() {
        this.player_visibility = true;
        this.geri_visibility = false;
        this.profession_format = "icon";
        this.profession_number_format = false;
    }

    public boolean isPlayer_visibility() {
        return player_visibility;
    }

    public void setPlayer_visibility(boolean player_visibility) {
        this.player_visibility = player_visibility;
    }

    public boolean isGeri_visibility() {
        return geri_visibility;
    }

    public void setGeri_visibility(boolean geri_visibility) {
        this.geri_visibility = geri_visibility;
    }

    public String getProfession_format() {
        return profession_format;
    }

    public void setProfession_format(String profession_format) {
        this.profession_format = profession_format;
    }

    public boolean isProfession_number_format() {
        return profession_number_format;
    }

    public void setProfession_number_format(boolean profession_number_format) {
        this.profession_number_format = profession_number_format;
    }
}
