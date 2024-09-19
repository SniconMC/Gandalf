package com.github.sniconmc.gandalf.config;

public class GandalfProfession {

    private String profession_id;
    private String profession_style;
    private String profession_style_sidebar;
    private String profession_icon;
    private String profession_icon_style;
    private double required_xp;

    private GandalfPermission permissions;

    public double getXpRequired() {
        return required_xp;
    }

    public String getProfession_id() {
        return profession_id;
    }

    public String getProfession_style() {
        return profession_style;
    }

    public String getProfession_style_sidebar() {
        return profession_style_sidebar;
    }

    public String getProfession_icon() {
        return profession_icon;
    }

    public String getProfession_icon_style() {
        return profession_icon_style;
    }

    public GandalfPermission getPermissions() {
        return permissions;
    }
}
