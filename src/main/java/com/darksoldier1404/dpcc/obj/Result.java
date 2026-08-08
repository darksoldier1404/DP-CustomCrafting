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
        inventory = new DInventory(CustomCrafting.getInstance().getLang().getWithArgs("result_edit_title", recipe.getName()), 54, CustomCrafting.getInstance());
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
        ensureDefaultWeights();
    }

    /**
     * 결과 아이템이 들어있는 모든 슬롯이 가중치를 갖도록 보정한다.
     * 가중치가 하나도 없으면 제작 시 결과가 뽑히지 않으므로, 미설정 슬롯은 기본값 1로 채운다.
     * 이미 관리자가 지정한 가중치는 그대로 두고, 아이템이 사라진 슬롯의 가중치는 제거한다.
     */
    public void ensureDefaultWeights() {
        ItemStack[] contents = inventory.getContents();
        weights.removeIf(weight -> {
            int slot = weight.getSlot();
            if (slot < 0 || slot >= contents.length) return true;
            ItemStack item = contents[slot];
            return item == null || item.getType().isAir();
        });
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item == null || item.getType().isAir()) continue;
            if (findWeight(i) == null) {
                weights.add(new ResultWeight(i, 1));
            }
        }
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
        data.set("Result." + parentRecipe.getName() + ".amount", resultAmount);
        for (int i = 0; i < inventory.getContents().length; i++) {
            data.set("Result." + parentRecipe.getName() + ".item." + i, inventory.getContents()[i]);
        }
        for (ResultWeight weight : weights) {
            weight.serialize(data, parentRecipe.getName());
        }
        return data;
    }

    public Result deserialize(YamlConfiguration data) {
        this.resultAmount = Math.max(1, data.getInt("Result." + parentRecipe.getName() + ".amount", 1));
        ItemStack[] contents = new ItemStack[inventory.getSize()];
        for (int i = 0; i < contents.length; i++) {
            contents[i] = data.getItemStack("Result." + parentRecipe.getName() + ".item." + i);
        }
        inventory.setContents(contents);

        List<ResultWeight> deserializedWeights = new ArrayList<>();
        String weightPath = "ResultWeight." + parentRecipe.getName();
        if (data.contains(weightPath)) {
            for (String key : data.getConfigurationSection(weightPath).getKeys(false)) {
                int slot = Integer.parseInt(key);
                int weight = data.getInt(weightPath + "." + key + ".weight");
                deserializedWeights.add(new ResultWeight(slot, weight));
            }
        }
        this.weights = deserializedWeights;
        // 가중치 정보가 아예 없는 (구버전/미설정) 데이터도 제작 가능하도록 기본값을 채운다.
        ensureDefaultWeights();
        return this;
    }
}
