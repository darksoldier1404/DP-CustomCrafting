package com.darksoldier1404.dpcc;

import com.darksoldier1404.dpcc.commands.DPCCCommand;
import com.darksoldier1404.dpcc.obj.Category;
import com.darksoldier1404.dpcc.obj.Result;
import com.darksoldier1404.dppc.annotation.DPPCoreVersion;
import com.darksoldier1404.dppc.data.DPlugin;
import com.darksoldier1404.dppc.data.DataContainer;
import com.darksoldier1404.dppc.data.DataType;
import com.darksoldier1404.dppc.utils.PluginUtil;
import com.darksoldier1404.dpcc.events.DPCCEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@DPPCoreVersion(since = "5.3.3")
public class CustomCrafting extends DPlugin {
    private static CustomCrafting plugin;
    private final DataContainer<String, Category> data;
    public static final Map<UUID, Result> currentEditingResult = new HashMap<>();
    public static final Map<UUID, Integer> currentEditingWeight = new HashMap<>();
    public static boolean showChanceInPreview;

    public CustomCrafting() {
        super(true);
        plugin = this;
        init();
        data = loadDataContainer(new DataContainer<>(this, DataType.CUSTOM, "data"), Category.class);
        showChanceInPreview = getConfig().getBoolean("Settings.showChanceInPreview");
    }

    public static CustomCrafting getInstance() {
        return plugin;
    }

    public DataContainer<String, Category> getData() {
        return data;
    }

    @Override
    public void onLoad() {
        PluginUtil.addPlugin(plugin, 28201);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new DPCCEvent(), plugin);
        DPCCCommand.init();
    }

    @Override
    public void onDisable() {
        saveAllData();
    }
}
