package com.darksoldier1404.dpcc.obj;

import com.darksoldier1404.dpcc.CustomCrafting;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.InventoryUtils;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Recipe {
    private String categoryName;
    private String name;
    private ItemStack[] ingredients = new ItemStack[20];
    private final List<Integer> slotSet = Arrays.asList(12, 13, 14, 15, 16, 21, 22, 23, 24, 25, 30, 31, 32, 33, 34, 39, 40, 41, 42, 43);
    private Result result;

    public Recipe(String categoryName, String name) {
        this.categoryName = categoryName;
        this.name = name;
        this.result = new Result(this);
    }

    public Recipe(String categoryName, String name, ItemStack[] ingredients, Result result) {
        this.categoryName = categoryName;
        this.name = name;
        this.ingredients = ingredients;
        this.result = result;
        this.result.setParentRecipe(this);
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getName() {
        return name;
    }

    public ItemStack[] getIngredients() {
        return ingredients;
    }

    public void setIngredients(ItemStack[] ingredients) {
        this.ingredients = ingredients;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }


    public void editIngredient(Player p) {
        DInventory inv = new DInventory("레시피 재료 편집 : " + name, 54, true, true, CustomCrafting.getInstance());
        inv.setObj(this);
        inv.setChannel(201);
        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta im = pane.getItemMeta();
        if (im != null) {
            im.setDisplayName(" ");
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            pane.setItemMeta(im);
        }
        NBT.setStringTag(pane, "dppc_clickcancel", "true");
        for (int i = 0; i < inv.getSize(); i++) {
            if (!slotSet.contains(i)) {
                inv.setItem(i, pane);
            }
        }
        for (int i = 0; i < ingredients.length; i++) {
            inv.setItem(slotSet.get(i), ingredients[i]);
        }
        inv.openInventory(p);
    }

    public void applyIngredient(DInventory inv) {
        for (int i = 0; i < ingredients.length; i++) {
            ingredients[i] = inv.getItem(slotSet.get(i));
        }
    }

    public void openRecipeCraft(Player p) {
        DInventory inv = new DInventory("레시피 제작 : " + name, 54, CustomCrafting.getInstance());
        ItemStack pane = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta im = pane.getItemMeta();
        if (im != null) {
            im.setDisplayName(" ");
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            pane.setItemMeta(im);
        }
        NBT.setStringTag(pane, "dppc_clickcancel", "true");
        for (int i = 0; i < inv.getSize(); i++) {
            if (!slotSet.contains(i)) {
                inv.setItem(i, pane);
            }
        }
        for (int i = 0; i < ingredients.length; i++) {
            inv.setItem(slotSet.get(i), ingredients[i]);
        }
        ItemStack confirm = new ItemStack(Material.GREEN_WOOL);
        ItemMeta cim = confirm.getItemMeta();
        if (cim != null) {
            cim.setDisplayName("§a제작하기");
            cim.setLore(Collections.singletonList("§7클릭하여 제작을 시도합니다."));
            cim.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            confirm.setItemMeta(cim);
        }
        NBT.setStringTag(confirm, "dpcc_confirm", "true");
        inv.setItem(49, confirm);
        inv.setItem(10, Arrays.stream(getResult().getInventory().getContents()).findFirst().isPresent() ? Arrays.stream(getResult().getInventory().getContents()).findFirst().get() : null);
        if (inv.getItem(10) != null) {
            ItemStack item = inv.getItem(10);
            im = item.getItemMeta();
            List<String> lore = im.hasLore() ? im.getLore() : new ArrayList<>();
            lore.add("§6클릭하여 모든 결과 아이템 확인.");
            item.setItemMeta(im);
            inv.setItem(10, item);
        }
        inv.setObj(this);
        inv.setChannel(100);
        inv.openInventory(p);
    }

    public boolean hasEnoughIngredients(Player p) {
        Set<ItemStack> required = new HashSet<>();
        for (ItemStack ingredient : ingredients) {
            if (ingredient != null) {
                required.add(ingredient);
            }
        }
        for (ItemStack reqItem : required) {
            int totalAmount = 0;
            for (ItemStack invItem : p.getInventory().getContents()) {
                if (invItem != null && invItem.isSimilar(reqItem)) {
                    totalAmount += invItem.getAmount();
                }
            }
            if (totalAmount < reqItem.getAmount()) {
                return false;
            }
        }
        return true;
    }

    public void craft(Player p) {
        if (!hasEnoughIngredients(p)) {
            p.sendMessage(CustomCrafting.getInstance().getPrefix() + "§c제작에 필요한 재료가 부족합니다.");
            return;
        }
        List<ItemStack> selected = getResultByWeight();
        if (InventoryUtils.hasEnoughSpace(p.getInventory().getStorageContents(), selected)) {
            for (ItemStack item : selected) p.getInventory().addItem(item);
            for (ItemStack ingredient : ingredients) {
                if (ingredient != null) {
                    p.getInventory().removeItem(ingredient);
                }
            }
            p.sendMessage(CustomCrafting.getInstance().getPrefix() + "§a제작이 완료되었습니다.");
        } else {
            p.sendMessage(CustomCrafting.getInstance().getPrefix() + "§c인벤토리 공간이 부족합니다.");
        }
    }

    public List<ItemStack> getResultByWeight() {
        int amount = result.getResultAmount();
        List<ItemStack> selected = new ArrayList<>();

        // Calculate total weight
        double totalWeight = 0;
        for (ResultWeight rw : result.getWeights()) {
            totalWeight += rw.getWeight();
        }

        for (int i = 0; i < amount; i++) {
            double randomValue = Math.random() * totalWeight;
            for (ResultWeight rw : result.getWeights()) {
                randomValue -= rw.getWeight();
                if (randomValue <= 0) {
                    ItemStack selectedItem = result.getInventory().getItem(rw.getSlot());
                    if (selectedItem != null) {
                        selected.add(selectedItem.clone()); // Clone to prevent modification issues
                    }
                    break;
                }
            }
        }
        return selected;
    }

    public YamlConfiguration serialize(YamlConfiguration data) {
        for (int i = 0; i < ingredients.length; i++) {
            data.set("Recipe." + name + ".ingredient." + i, ingredients[i]);
        }
        data = result.serialize(data);
        return data;
    }

    public void deserialize(YamlConfiguration data, String name) {
        this.categoryName = data.getString("name");
        this.name = name;
        for (int i = 0; i < ingredients.length; i++) {
            ingredients[i] = data.getItemStack("Recipe." + name + ".ingredient." + i);
        }
        this.result = new Result(this).deserialize(data);
    }
}
