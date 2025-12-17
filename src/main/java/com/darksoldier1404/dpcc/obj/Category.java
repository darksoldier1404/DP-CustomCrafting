package com.darksoldier1404.dpcc.obj;

import com.darksoldier1404.dpcc.CustomCrafting;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.data.DataCargo;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class Category implements DataCargo {
    private String name;
    private DInventory inventory; // multi page inventory
    private Map<String, Recipe> recipes = new HashMap<>();

    public Category() {
    }

    public Category(String name, DInventory inventory, Map<String, Recipe> recipes) {
        this.name = name;
        this.inventory = inventory;
        this.recipes = recipes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DInventory getInventory() {
        return inventory;
    }

    public void setInventory(DInventory inventory) {
        this.inventory = inventory;
    }

    public Map<String, Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(Map<String, Recipe> recipes) {
        this.recipes = recipes;
    }

    public void openList(Player p) {
        DInventory inv = inventory.clone();
        inv.setChannel(0);
        inv.setObj(this);
        for (Recipe r : recipes.values()) {
            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta im = item.getItemMeta();
            if (im != null) {
                im.setDisplayName("§e레시피: §f" + r.getName());
                im.setLore(Collections.singletonList("§7클릭하여 해당 레시피 제작."));
                im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(im);
            }
            inv.addPageItem(NBT.setStringTag(item, "dpcc_recipe_name", r.getName()));
        }
        inv.turnPage(0);
        inv.openInventory(p);
    }

    @Override
    public YamlConfiguration serialize() {
        YamlConfiguration data = new YamlConfiguration();
        data.set("name", name);
        for (Recipe r : recipes.values()) {
            data = r.serialize(data);
        }
        return data;
    }

    @Override
    public Category deserialize(YamlConfiguration data) {
        this.name = data.getString("name");
        this.inventory = new DInventory("레시피 목록 : " + name, 54, true, true, CustomCrafting.getInstance()).deserialize(data);
        if (data.contains("Recipe")) {
            for (String key : data.getConfigurationSection("Recipe").getKeys(false)) {
                Recipe r = new Recipe(name, key);
                r.deserialize(data, key);
                recipes.put(key, r);
            }
        }
        return this;
    }
}
