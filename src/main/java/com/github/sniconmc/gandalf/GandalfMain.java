package com.github.sniconmc.gandalf;

import com.github.sniconmc.gandalf.database.DatabaseManager;
import com.github.sniconmc.utils.UtilsMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.sniconmc.gandalf.listener.SetPlayerChat;
import com.github.sniconmc.gandalf.listener.SetPlayerTab;

public class GandalfMain {

    public static final Logger logger = LoggerFactory.getLogger(UtilsMain.class);

    public static DatabaseManager dbManager;

    public static void init() {
        logger.info("Gandalf initialized");

        // Initialize your DatabaseManager
        dbManager = new DatabaseManager();
        dbManager.connect();
        dbManager.createTable();

        GandalfManager gandalfManager = new GandalfManager();

        SetPlayerTab removePlayerTab = new SetPlayerTab();
        SetPlayerChat onChat = new SetPlayerChat();
    }
}
