package com.github.sniconmc.gandalf.config;

public class GandalfRank {

    private String rank_id;
    private String rank_style;
    private String rank_color;
    private String rank_chat_color;

    private String rank_format;
    private String rank_format_simple;

    private GandalfPermission permissions;

    public String getRankId() {
        return rank_id;
    }

    public String getRankStyle() {
        return rank_style;
    }

    public String getRankFormat() {
        return rank_format;
    }

    public String getRankColor() {
        return rank_color;
    }

    public String getRankChatColor() {
        return rank_chat_color;
    }

    public String getRankFormatSimple() {
        return rank_format_simple;
    }

    public GandalfPermission getPermissions() {
        return permissions;
    }
}
