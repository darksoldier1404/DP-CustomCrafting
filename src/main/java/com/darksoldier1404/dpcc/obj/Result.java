package com.darksoldier1404.dpcc.obj;

import com.darksoldier1404.dpcc.CustomCrafting;
import com.darksoldier1404.dpcc.functions.DPCCFunction;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Result {
    private Recipe parentRecipe;
    private DInventory inventory;
    private int resultAmount;
    private List<ResultWeight> weights = new ArrayList<>();

    public Result(Recipe recipe) {
        parentRecipe = recipe;
        resultAmount = 1;
        inventory = new DInventory("Result Item Edit - " + recipe.getName(), 54, CustomCrafting.getInstance());
    }

    public Result(Recipe recipe, DInventory inventory, List<ResultWeight> weights) {
        this.parentRecipe = recipe;
        this.inventory = inventory;
        this.resultAmount = 1;
        this.weights = weights;
    }

    public Recipe getParentRecipe() {
        return parentRecipe;
    }

    public void setParentRecipe(Recipe parentRecipe) {
        this.parentRecipe = parentRecipe;
    }

    public DInventory getInventory() {
        return inventory;
    }

    public void setInventory(DInventory inventory) {
        this.inventory = inventory;
    }

    public int getResultAmount() {
        return resultAmount;
    }

    public void setResultAmount(int resultAmount) {
        this.resultAmount = resultAmount;
    }

    public List<ResultWeight> getWeights() {
        return weights;
    }

    public void setWeights(List<ResultWeight> weights) {
        this.weights = weights;
    }

    public void editResultItem(Player p) {
        inventory.setChannel(301);
        inventory.setObj(this);
        inventory.openInventory(p);
    }

    public void applyItems(ItemStack[] items) {
        inventory.setContents(items);
    }

    @Nullable
    public ResultWeight findWeight(int slot) {
        for (ResultWeight weight : weights) {
            if (weight.getSlot() == slot) {
                return weight;
            }
        }
        return null;
    }

    public void editWeight(Player p) {
        DInventory inv = inventory.clone();
        inv.setChannel(302);
        inv.setObj(this);
        inv.openInventory(p);
        DPCCFunction.updateChanceLore(inv);
        CustomCrafting.currentEditingResult.put(p.getUniqueId(), this);
    }

    public void preview(Player p) {
        DInventory inv = inventory.clone();
        inv.setChannel(300);
        inv.setObj(this);
        inv.openInventory(p);
        if (CustomCrafting.showChanceInPreview) {
            DPCCFunction.updateChanceLore(inv);
        }
    }

    public YamlConfiguration serialize(YamlConfiguration data) {
        data.set("Result.amount", resultAmount);
        for (int i = 0; i < inventory.getContents().length; i++) {
            data.set("Result.item." + i, inventory.getContents()[i]);
        }
        for (ResultWeight weight : weights) {
            weight.serialize(data);
        }
        return data;
    }

    public Result deserialize(YamlConfiguration data) {
        this.resultAmount = data.getInt("Result.amount");
        ItemStack[] contents = new ItemStack[inventory.getSize()];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = data.getItemStack("Result.item." + i);
        }
        inventory.setContents(contents);

        List<ResultWeight> deserializedWeights = new ArrayList<>();
        if (data.contains("ResultWeight")) {
            for (String key : data.getConfigurationSection("ResultWeight").getKeys(false)) {
                int slot = Integer.parseInt(key);
                int weight = data.getInt("ResultWeight." + key + ".weight");
                deserializedWeights.add(new ResultWeight(slot, weight));
            }
        }
        this.weights = deserializedWeights;
        return this;
    }
}
