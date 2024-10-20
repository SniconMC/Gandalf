package com.github.sniconmc.gandalf.database;

public class DatabasePlayer {
    private String uuid;
    private String name;
    private String rankId;
    private String profession;
    private String oldProfession;
    private double emeralds;
    private int achievements;
    private double professionTotalXP;
    private boolean playerVisibility;
    private boolean geriVisibility;
    private String professionFormat;
    private boolean professionNumberFormat;
    private long lastLoginTime;
    private String ip;

    // Constructor that takes only uuid and name, setting defaults for other fields
    public DatabasePlayer(String uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.rankId = "villager"; // Default rank
        this.profession = "nitwit"; // Default profession
        this.oldProfession = ""; // No old profession by default
        this.emeralds = 0.0; // Default emeralds
        this.achievements = 0; // Default achievements
        this.professionTotalXP = 0.0; // Default XP
        this.playerVisibility = true; // Default to visible
        this.geriVisibility = false; // Default to not visible for geri
        this.professionFormat = "icon"; // Default profession format
        this.professionNumberFormat = false; // Default number format
        this.lastLoginTime = System.currentTimeMillis(); // Set to current time
    }

    // Full constructor with all fields
    public DatabasePlayer(String uuid, String name, String rankId, String profession, String oldProfession,
                          double emeralds, int achievements, double professionTotalXP, boolean playerVisibility,
                          boolean geriVisibility, String professionFormat, boolean professionNumberFormat,
                          long lastLoginTime) {
        this.uuid = uuid;
        this.name = name;
        this.rankId = rankId;
        this.profession = profession;
        this.oldProfession = oldProfession;
        this.emeralds = emeralds;
        this.achievements = achievements;
        this.professionTotalXP = professionTotalXP;
        this.playerVisibility = playerVisibility;
        this.geriVisibility = geriVisibility;
        this.professionFormat = professionFormat;
        this.professionNumberFormat = professionNumberFormat;
        this.lastLoginTime = lastLoginTime;
    }

    // Getters and setters for each field (optional)
    public String getUuid() { return uuid; }
    public String getName() { return name; }
    public String getRankId() { return rankId; }
    public String getProfession() { return profession; }
    public String getOldProfession() { return oldProfession; }
    public double getEmeralds() { return emeralds; }
    public int getAchievements() { return achievements; }
    public double getProfessionTotalXP() { return professionTotalXP; }
    public boolean isPlayerVisibility() { return playerVisibility; }
    public boolean isGeriVisibility() { return geriVisibility; }
    public String getProfessionFormat() { return professionFormat; }
    public boolean isProfessionNumberFormat() { return professionNumberFormat; }
    public long getLastLoginTime() { return lastLoginTime; }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setRankId(String rankId) {
        this.rankId = rankId;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }
    public void setOldProfession(String oldProfession) {
        this.oldProfession = oldProfession;
    }
    public void setEmeralds(double emeralds) {
        this.emeralds = emeralds;
    }
    public void setAchievements(int achievements) {
        this.achievements = achievements;
    }
    public void setProfessionTotalXP(double professionTotalXP) {
        this.professionTotalXP = professionTotalXP;
    }
    public void setPlayerVisibility(boolean playerVisibility) {
        this.playerVisibility = playerVisibility;
    }
    public void setGeriVisibility(boolean geriVisibility) {
        this.geriVisibility = geriVisibility;
    }
    public void setProfessionFormat(String professionFormat) {
        this.professionFormat = professionFormat;
    }
    public void setProfessionNumberFormat(boolean professionNumberFormat) {
        this.professionNumberFormat = professionNumberFormat;
    }
    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    // toString method to print the player details (optional)
    @Override
    public String toString() {
        return "Player{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", rankId='" + rankId + '\'' +
                ", profession='" + profession + '\'' +
                ", oldProfession='" + oldProfession + '\'' +
                ", emeralds=" + emeralds +
                ", achievements=" + achievements +
                ", professionTotalXP=" + professionTotalXP +
                ", playerVisibility=" + playerVisibility +
                ", geriVisibility=" + geriVisibility +
                ", professionFormat='" + professionFormat + '\'' +
                ", professionNumberFormat=" + professionNumberFormat +
                ", lastLoginTime=" + lastLoginTime +
                '}';
    }
}
