package com.darksoldier1404.dpcc.functions;

import com.darksoldier1404.dpcc.CustomCrafting;
import com.darksoldier1404.dpcc.obj.Category;
import com.darksoldier1404.dpcc.obj.Recipe;
import com.darksoldier1404.dpcc.obj.Result;
import com.darksoldier1404.dpcc.obj.ResultWeight;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class DPCCFunction {
    private static final CustomCrafting plugin = CustomCrafting.getInstance();

    private DPCCFunction() {
        throw new IllegalStateException("Utility class");
    }

    public static boolean isExistingCategory(String name) {
        return plugin.getData().containsKey(name);
    }

    public static void createCategory(Player p, String name) {
        if (isExistingCategory(name)) {
            p.sendMessage(plugin.getPrefix() + "§c이미 존재하는 카테고리 이름입니다.");
            return;
        }
        Category category = new Category();
        DInventory inventory = new DInventory("레시피 목록 : " + name, 54, true, true, plugin);
        category.setInventory(inventory);
        category.setName(name);
        plugin.getData().put(name, category);
        plugin.getData().save(name);
        p.sendMessage(plugin.getPrefix() + "§a카테고리 §f" + name + "§a(이)가 생성되었습니다.");
    }

    public static boolean isExistingRecipe(String categoryName, String recipeName) {
        if (!isExistingCategory(categoryName)) {
            return false;
        }
        Category category = plugin.getData().get(categoryName);
        return category.getRecipes().containsKey(recipeName);
    }

    public static boolean checkValidCategoryAndRecipe(Player p, String categoryName, String recipeName) {
        if (!isExistingCategory(categoryName)) {
            p.sendMessage(plugin.getPrefix() + "§c존재하지 않는 카테고리 이름입니다.");
            return false;
        }
        if (!isExistingRecipe(categoryName, recipeName)) {
            p.sendMessage(plugin.getPrefix() + "§c존재하지 않는 레시피 이름입니다.");
            return false;
        }
        return true;
    }

    public static void createRecipe(Player p, String categoryName, String recipeName) {
        if (!isExistingCategory(categoryName)) {
            p.sendMessage(plugin.getPrefix() + "§c존재하지 않는 카테고리 이름입니다.");
            return;
        }
        Category category = plugin.getData().get(categoryName);
        category.getRecipes().put(recipeName, new Recipe(categoryName, recipeName));
        plugin.getData().put(categoryName, category);
        plugin.getData().save(categoryName);
        p.sendMessage(plugin.getPrefix() + "§a레시피 §f" + recipeName + "§a(이)가 생성되었습니다.");
    }

    public static void openRecipeItemSettingInventory(Player p, String categoryName, String recipeName) {
        if (!checkValidCategoryAndRecipe(p, categoryName, recipeName)) {
            return;
        }
        Category category = plugin.getData().get(categoryName);
        Recipe recipe = category.getRecipes().get(recipeName);
        recipe.editIngredient(p);
    }

    public static void saveRecipeItem(Player p, DInventory inv) {
        Recipe recipe = (Recipe) inv.getObj();
        String categoryName = recipe.getCategoryName();
        recipe.applyIngredient(inv);
        Category category = plugin.getData().get(categoryName);
        category.getRecipes().put(recipe.getName(), recipe);
        plugin.getData().put(categoryName, category);
        plugin.getData().save(categoryName);
        p.sendMessage(plugin.getPrefix() + "§a레시피 재료가 저장되었습니다.");
    }

    public static void openResultItemSettingInventory(Player p, String categoryName, String recipeName) {
        if (!checkValidCategoryAndRecipe(p, categoryName, recipeName)) {
            return;
        }
        Category category = plugin.getData().get(categoryName);
        Recipe recipe = category.getRecipes().get(recipeName);
        recipe.getResult().editResultItem(p);
    }

    public static void saveResultItem(Player p, DInventory inv) {
        Result result = (Result) inv.getObj();
        Recipe recipe = result.getParentRecipe();
        String categoryName = recipe.getCategoryName();
        recipe.getResult().applyItems(inv.getContents());
        Category category = plugin.getData().get(categoryName);
        category.getRecipes().put(recipe.getName(), recipe);
        plugin.getData().put(categoryName, category);
        plugin.getData().save(categoryName);
        p.sendMessage(plugin.getPrefix() + "§a결과 아이템이 저장되었습니다.");
    }

    public static void setResultAmount(Player p, String categoryName, String recipeName, int amount) {
        if (!checkValidCategoryAndRecipe(p, categoryName, recipeName)) {
            return;
        }
        Category category = plugin.getData().get(categoryName);
        Recipe recipe = category.getRecipes().get(recipeName);
        recipe.getResult().setResultAmount(amount);
        category.getRecipes().put(recipeName, recipe);
        plugin.getData().put(categoryName, category);
        plugin.getData().save(categoryName);
        p.sendMessage(plugin.getPrefix() + "§a결과 아이템 수량이 §f" + amount + "§a(으)로 설정되었습니다.");
    }

    public static void openResultWeightSettingInventory(Player p, String categoryName, String recipeName) {
        if (!checkValidCategoryAndRecipe(p, categoryName, recipeName)) {
            return;
        }
        Category category = plugin.getData().get(categoryName);
        Recipe recipe = category.getRecipes().get(recipeName);
        recipe.getResult().editWeight(p);
    }

    public static void editResultWeightWithChat(Player p, int slot) {
        plugin.currentEditingWeight.put(p.getUniqueId(), slot);
    }

    public static void setResultWeightFromChat(Player p, String message) {
        if (!plugin.currentEditingWeight.containsKey(p.getUniqueId())) {
            p.sendMessage(plugin.getPrefix() + "§c편집 중인 결과 아이템이 없습니다.");
            return;
        }
        int slot = plugin.currentEditingWeight.get(p.getUniqueId());
        Result result = plugin.currentEditingResult.get(p.getUniqueId());
        ResultWeight weight = result.findWeight(slot);
        Recipe recipe = result.getParentRecipe();
        int weightValue;
        try {
            weightValue = Integer.parseInt(message);
        } catch (NumberFormatException e) {
            p.sendMessage(plugin.getPrefix() + "§c유효한 숫자가 아닙니다. 다시 시도해주세요.");
            return;
        }
        if (weight == null) {
            weight = new ResultWeight(slot, weightValue);
            recipe.getResult().getWeights().add(weight);
        } else {
            weight.setWeight(weightValue);
        }
        String categoryName = recipe.getCategoryName();
        Category category = plugin.getData().get(categoryName);
        category.getRecipes().put(recipe.getName(), recipe);
        plugin.getData().put(categoryName, category);
        plugin.getData().save(categoryName);
        p.sendMessage(plugin.getPrefix() + "§a결과 아이템의 가중치가 §f" + weightValue + "§a(으)로 설정되었습니다.");
        plugin.currentEditingWeight.remove(p.getUniqueId());
        Bukkit.getScheduler().runTaskLater(plugin, () -> result.editWeight(p), 1L);
    }

    public static void updateChanceLore(DInventory inv) {
        Result result = (Result) inv.getObj();
        if (result == null) return;
        int totalWeight = 0;
        for (int i = 0; i < inv.getContents().length; i++) {
            if (inv.getContents()[i] == null) continue;
            ResultWeight rw = result.findWeight(i);
            if (rw != null) {
                totalWeight += rw.getWeight();
            }
        }
        for (int i = 0; i < inv.getContents().length; i++) {
            ItemStack item = inv.getContents()[i];
            if (item == null || item.getType().isAir()) {
                continue;
            }
            ResultWeight rw = result.findWeight(i);
            if(rw != null) {
                int weight = rw.getWeight();
                List<String> lore = item.getItemMeta() != null && item.getItemMeta().getLore() != null ? item.getItemMeta().getLore() : new ArrayList<>();
                if (weight > 0) {
                    lore.add("§7Weight: §e" + weight);
                    double chance = (double) weight / (double) totalWeight * 100.0;
                    lore.add("§7Chance: §e" + String.format("%.2f", chance) + "%");
                }
                ItemMeta meta = item.getItemMeta();
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
        }
    }

    public static void openCategoryInventory(Player p, String categoryName) {
        if (!isExistingCategory(categoryName)) {
            p.sendMessage(plugin.getPrefix() + "§c존재하지 않는 카테고리 이름입니다.");
            return;
        }
        Category category = plugin.getData().get(categoryName);
        category.openList(p);
    }

    public static void openRecipeCraft(Player p, Category category, String recipeName) {
        Recipe r = category.getRecipes().get(recipeName);
        r.openRecipeCraft(p);
    }
}
