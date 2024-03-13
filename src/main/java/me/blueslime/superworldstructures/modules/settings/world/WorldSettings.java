package me.blueslime.superworldstructures.modules.settings.world;

import java.util.List;

public class WorldSettings {
    private final List<String> structures;

    private final boolean enabled;

    public WorldSettings(boolean enabled, List<String> structures) {
        this.structures = structures;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isEmpty(){
        return getStructures().isEmpty();
    }

    public List<String> getStructures() {
        return structures;
    }
}
