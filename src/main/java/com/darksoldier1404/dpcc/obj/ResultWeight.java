package com.darksoldier1404.dpcc.obj;

import org.bukkit.configuration.file.YamlConfiguration;

public class ResultWeight {
    private int slot;
    private int weight;

    public ResultWeight(int slot, int weight) {
        this.slot = slot;
        this.weight = weight;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public YamlConfiguration serialize(YamlConfiguration data) {
        data.set("ResultWeight." + slot + ".weight", weight);
        return data;
    }
}
