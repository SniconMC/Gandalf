package com.github.sniconmc.gandalf;

import java.io.File;

public enum ProfileFolders {
    PROFILE_FOLDER(new File("resources/profiles")),
    PROFESSION_FOLDER(new File("resources/gandalf/professions")),
    RANK_FOLDER(new File("resources/gandalf/ranks"));

    private final File folder;

    // Constructor to initialize the folder path for each constant
    ProfileFolders(File folder) {
        this.folder = folder;
    }

    // Getter to access the File object
    public File getFolder() {
        return folder;
    }
}
