package com.github.sniconmc.gandalf.config;

import java.util.List;

public class GandalfPermission {

    private String parent;
    private List<String> specific_permissions;


    public String getParent() {
        return parent;
    }

    public List<String> getSpecific() {
        return specific_permissions;
    }
}
