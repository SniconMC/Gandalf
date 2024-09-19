package com.github.sniconmc.gandalf;

import com.github.sniconmc.utils.UtilsMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.sniconmc.gandalf.listener.SetPlayerChat;
import com.github.sniconmc.gandalf.listener.SetPlayerTab;

public class GandalfMain {

    public static final Logger logger = LoggerFactory.getLogger(UtilsMain.class);

    public static void init() {
        logger.info("Gandalf initialized");

        GandalfManager gandalfManager = new GandalfManager();

        SetPlayerTab removePlayerTab = new SetPlayerTab();
        SetPlayerChat onChat = new SetPlayerChat();
    }
}
