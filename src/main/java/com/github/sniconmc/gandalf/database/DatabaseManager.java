package com.github.sniconmc.gandalf.database;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.sql.*;
import java.util.Objects;

public class DatabaseManager {
    private Connection connection;

    // Helper method to load JSON credentials
    private JsonObject loadCredentials() {
        Gson gson = new Gson();
        JsonObject credentials = null;

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("login.json");
             InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(inputStream))) {

            credentials = gson.fromJson(reader, JsonObject.class);
        } catch (Exception e) {
            System.out.println("Error loading credentials: " + e.getMessage());
            e.printStackTrace();
        }

        return credentials;
    }

    // Establishes connection to MySQL
    public void connect() {
        JsonObject credentials = loadCredentials();
        if (credentials == null) {
            System.out.println("Could not load credentials.");
            return;
        }

        String url = "jdbc:mysql://localhost:53306/minestomdb";
        String user = credentials.get("user").getAsString();
        String password = credentials.get("password").getAsString();

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to MySQL database!");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Checks if the connection is null before executing SQL commands
    public void createTable() {
        if (connection == null) {
            System.out.println("Cannot create table. No database connection.");
            return;
        }

        String sql = "CREATE TABLE IF NOT EXISTS players ("
                + " uuid VARCHAR(36) PRIMARY KEY, "  // UUID as a string
                + " username VARCHAR(255) NOT NULL, "
                + " rank_id VARCHAR(255), "
                + " profession VARCHAR(255), "
                + " old_profession VARCHAR(255), "
                + " emeralds DECIMAL(10,2) DEFAULT 0, "  // Using decimal for currency-related values
                + " achievements INT DEFAULT 0, "
                + " profession_total_xp DECIMAL(10,2) DEFAULT 0, "
                + " player_visibility BOOLEAN DEFAULT TRUE, "  // Stored as part of settings
                + " geri_visibility BOOLEAN DEFAULT FALSE, "    // Stored as part of settings
                + " profession_format VARCHAR(255) DEFAULT 'icon', "
                + " profession_number_format BOOLEAN DEFAULT FALSE, "
                + " last_login_time BIGINT "  // Timestamp as BIGIN
                + ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void insertPlayer(DatabasePlayer player) {
        if (connection == null) {
            System.out.println("Cannot insert player. No database connection.");
            return;
        }

        // Check if the player already exists
        String checkSql = "SELECT COUNT(*) FROM players WHERE uuid = ?";
        String insertSql = "INSERT INTO players (uuid, username, rank_id, profession, old_profession, emeralds, achievements, profession_total_xp, player_visibility, geri_visibility, profession_format, profession_number_format, last_login_time) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String updateSql = "UPDATE players SET username = ?, rank_id = ?, profession = ?, old_profession = ?, emeralds = ?, achievements = ?, profession_total_xp = ?, player_visibility = ?, geri_visibility = ?, profession_format = ?, profession_number_format = ?, last_login_time = ? WHERE uuid = ?";

        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setString(1, player.getUuid());
            try (ResultSet rs = checkStmt.executeQuery()) {
                rs.next();
                int count = rs.getInt(1);  // Get the number of records with the same UUID

                if (count > 0) {
                    // Player exists, update the record
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                        updateStmt.setString(1, player.getName());
                        updateStmt.setString(2, player.getRankId());
                        updateStmt.setString(3, player.getProfession());
                        updateStmt.setString(4, player.getOldProfession());
                        updateStmt.setDouble(5, player.getEmeralds());
                        updateStmt.setInt(6, player.getAchievements());
                        updateStmt.setDouble(7, player.getProfessionTotalXP());
                        updateStmt.setBoolean(8, player.isPlayerVisibility());
                        updateStmt.setBoolean(9, player.isGeriVisibility());
                        updateStmt.setString(10, player.getProfessionFormat());
                        updateStmt.setBoolean(11, player.isProfessionNumberFormat());
                        updateStmt.setLong(12, player.getLastLoginTime());
                        updateStmt.setString(13, player.getUuid());  // WHERE uuid = ?
                        updateStmt.executeUpdate();
                        System.out.println("Updated player: " + player.getName());
                    }
                } else {
                    // Player does not exist, insert a new record
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                        insertStmt.setString(1, player.getUuid());
                        insertStmt.setString(2, player.getName());
                        insertStmt.setString(3, player.getRankId());
                        insertStmt.setString(4, player.getProfession());
                        insertStmt.setString(5, player.getOldProfession());
                        insertStmt.setDouble(6, player.getEmeralds());
                        insertStmt.setInt(7, player.getAchievements());
                        insertStmt.setDouble(8, player.getProfessionTotalXP());
                        insertStmt.setBoolean(9, player.isPlayerVisibility());
                        insertStmt.setBoolean(10, player.isGeriVisibility());
                        insertStmt.setString(11, player.getProfessionFormat());
                        insertStmt.setBoolean(12, player.isProfessionNumberFormat());
                        insertStmt.setLong(13, player.getLastLoginTime());
                        insertStmt.executeUpdate();
                        System.out.println("Inserted new player: " + player.getName());
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error inserting or updating player: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public DatabasePlayer getPlayer(String uuid) {
        if (connection == null) {
            System.out.println("Cannot retrieve player. No database connection.");
            return null;
        }

        String sql = "SELECT uuid, username, rank_id, profession, old_profession, emeralds, achievements, "
                + "profession_total_xp, player_visibility, geri_visibility, profession_format, "
                + "profession_number_format, last_login_time "
                + "FROM players WHERE uuid = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);  // Set the UUID in the query

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Retrieve all columns and create a DatabasePlayer object
                    return new DatabasePlayer(
                            rs.getString("uuid"),
                            rs.getString("username"),
                            rs.getString("rank_id"),
                            rs.getString("profession"),
                            rs.getString("old_profession"),
                            rs.getDouble("emeralds"),
                            rs.getInt("achievements"),
                            rs.getDouble("profession_total_xp"),
                            rs.getBoolean("player_visibility"),
                            rs.getBoolean("geri_visibility"),
                            rs.getString("profession_format"),
                            rs.getBoolean("profession_number_format"),
                            rs.getLong("last_login_time")
                    );
                } else {
                    System.out.println("Player not found with UUID: " + uuid);
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving player: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
