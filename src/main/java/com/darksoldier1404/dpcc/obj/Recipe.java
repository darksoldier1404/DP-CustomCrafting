package com.darksoldier1404.dpcc.obj;

import com.darksoldier1404.dpcc.CustomCrafting;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.utils.InventoryUtils;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        DInventory inv = new DInventory(CustomCrafting.getInstance().getLang().getWithArgs("ingredient_edit_title", name), 54, true, true, CustomCrafting.getInstance());
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
        DInventory inv = new DInventory(CustomCrafting.getInstance().getLang().getWithArgs("craft_title", name), 54, CustomCrafting.getInstance());
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
            cim.setDisplayName(CustomCrafting.getInstance().getLang().get("craft_button"));
            cim.setLore(Collections.singletonList(CustomCrafting.getInstance().getLang().get("click_to_attempt_craft")));
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
            lore.add(CustomCrafting.getInstance().getLang().get("click_to_preview_results"));
            item.setItemMeta(im);
            inv.setItem(10, item);
        }
        inv.setObj(this);
        inv.setChannel(100);
        inv.openInventory(p);
    }

    public boolean hasEnoughIngredients(Player p) {
        // 요구 아이템 → (템, 총 필요 개수) 맵
        Map<ItemStack, Integer> required = Arrays.stream(ingredients)
                .filter(Objects::nonNull)
                .filter(i -> !i.getType().isAir())
                .collect(Collectors.toMap(
                        Function.identity(),          // key = ItemStack 자체
                        ItemStack::getAmount,         // 초기 개수
                        Integer::sum,                 // 같은 아이템이면 amount 합산
                        LinkedHashMap::new            // 순서 유지 (선택)
                ));

        // 플레이어 인벤토리에서 실제 개수 계산
        for (Map.Entry<ItemStack, Integer> entry : required.entrySet()) {
            ItemStack prototype = entry.getKey();
            int needed = entry.getValue();

            int has = Arrays.stream(p.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .filter(item -> item.isSimilar(prototype))
                    .mapToInt(ItemStack::getAmount)
                    .sum();

            if (has < needed) {
                return false;
            }
        }

        return true;
    }

    public static boolean hasEnoughSpace(ItemStack[] content, List<ItemStack> items) {
        if (content != null) {
            Inventory inv = Bukkit.createInventory(null, 36);
            inv.setContents(content);
            // clone each item to prevent addItem() from modifying the original ItemStack amounts
            ItemStack[] itemsCopy = new ItemStack[items.size()];
            for (int i = 0; i < items.size(); i++) {
                itemsCopy[i] = items.get(i) != null ? items.get(i).clone() : null;
            }
            HashMap<Integer, ItemStack> leftover = new HashMap<>(inv.addItem(itemsCopy));
            return leftover.isEmpty();
        } else {
            return false;
        }
    }

    public void craft(Player p) {
        if (!hasEnoughIngredients(p)) {
            p.sendMessage(CustomCrafting.getInstance().getPrefix() +
                    CustomCrafting.getInstance().getLang().get("not_enough_ingredients"));
            return;
        }

        List<ItemStack> selected = getResultByWeight();
        if (selected.isEmpty()) {
            // 결과 아이템/가중치가 설정되지 않은 레시피. 재료만 소모되고 아무것도 지급되지 않는 것을 막는다.
            p.sendMessage(CustomCrafting.getInstance().getPrefix() +
                    CustomCrafting.getInstance().getLang().get("no_result_configured"));
            return;
        }

        ItemStack[] storage = p.getInventory().getStorageContents();
        if (!hasEnoughSpace(storage, selected)) {
            p.sendMessage(CustomCrafting.getInstance().getPrefix() +
                    CustomCrafting.getInstance().getLang().get("not_enough_inventory_space"));
            return;
        }
        Map<ItemStack, Integer> toRemove = new HashMap<>();

        for (ItemStack ing : ingredients) {
            if (ing == null || ing.getType().isAir()) continue;

            boolean merged = false;
            for (Map.Entry<ItemStack, Integer> entry : toRemove.entrySet()) {
                if (entry.getKey().isSimilar(ing)) {
                    entry.setValue(entry.getValue() + ing.getAmount());
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                toRemove.put(ing.clone(), ing.getAmount());  // clone to avoid reference issues
            }
        }
        for (Map.Entry<ItemStack, Integer> entry : toRemove.entrySet()) {
            ItemStack prototype = entry.getKey();
            int amountLeft = entry.getValue();

            for (int i = 0; i < p.getInventory().getSize(); i++) {
                ItemStack item = p.getInventory().getItem(i);
                if (item == null || !item.isSimilar(prototype)) continue;

                int canTake = Math.min(item.getAmount(), amountLeft);
                if (canTake == 0) continue;

                if (canTake == item.getAmount()) {
                    p.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - canTake);
                    p.getInventory().setItem(i, item);
                }

                amountLeft -= canTake;
                if (amountLeft <= 0) break;
            }
        }
        for (ItemStack item : selected) {
            p.getInventory().addItem(item);
        }

        p.sendMessage(CustomCrafting.getInstance().getPrefix() +
                CustomCrafting.getInstance().getLang().get("crafting_completed"));
    }

    public void craftAll(Player p) {
        if (!hasEnoughIngredients(p)) {
            p.sendMessage(CustomCrafting.getInstance().getPrefix() +
                    CustomCrafting.getInstance().getLang().get("not_enough_ingredients"));
            return;
        }

        Map<ItemStack, Integer> requiredPerCraft = new HashMap<>();
        for (ItemStack ing : ingredients) {
            if (ing == null || ing.getType().isAir()) continue;

            boolean merged = false;
            for (Map.Entry<ItemStack, Integer> entry : requiredPerCraft.entrySet()) {
                if (entry.getKey().isSimilar(ing)) {
                    entry.setValue(entry.getValue() + ing.getAmount());
                    merged = true;
                    break;
                }
            }
            if (!merged) {
                requiredPerCraft.put(ing.clone(), ing.getAmount());
            }
        }

        if (requiredPerCraft.isEmpty()) {
            return;
        }

        int maxCraftCount = Integer.MAX_VALUE;
        for (Map.Entry<ItemStack, Integer> entry : requiredPerCraft.entrySet()) {
            ItemStack prototype = entry.getKey();
            int neededPerCraft = entry.getValue();

            int has = Arrays.stream(p.getInventory().getContents())
                    .filter(Objects::nonNull)
                    .filter(item -> item.isSimilar(prototype))
                    .mapToInt(ItemStack::getAmount)
                    .sum();

            maxCraftCount = Math.min(maxCraftCount, has / neededPerCraft);
        }

        if (maxCraftCount <= 0) {
            p.sendMessage(CustomCrafting.getInstance().getPrefix() +
                    CustomCrafting.getInstance().getLang().get("not_enough_ingredients"));
            return;
        }

        List<ItemStack> selected = new ArrayList<>();
        for (int i = 0; i < maxCraftCount; i++) {
            selected.addAll(getResultByWeight());
        }
        if (selected.isEmpty()) {
            // 결과 아이템/가중치가 설정되지 않은 레시피. 재료만 소모되고 아무것도 지급되지 않는 것을 막는다.
            p.sendMessage(CustomCrafting.getInstance().getPrefix() +
                    CustomCrafting.getInstance().getLang().get("no_result_configured"));
            return;
        }

        // 재료 제거 후 인벤토리 상태를 시뮬레이션하여 정확한 공간 체크 수행
        // (재료 제거 전 storage로 체크하면, 재료가 차지하는 슬롯을 고려하지 못해 거짓 실패 발생)
        Inventory simInv = Bukkit.createInventory(null, 36);
        ItemStack[] storageContents = p.getInventory().getStorageContents();
        ItemStack[] storageCopy = new ItemStack[storageContents.length];
        for (int i = 0; i < storageContents.length; i++) {
            storageCopy[i] = storageContents[i] != null ? storageContents[i].clone() : null;
        }
        simInv.setContents(storageCopy);
        for (Map.Entry<ItemStack, Integer> entry : requiredPerCraft.entrySet()) {
            ItemStack prototype = entry.getKey();
            int amountLeft = entry.getValue() * maxCraftCount;
            for (int i = 0; i < simInv.getSize(); i++) {
                ItemStack item = simInv.getItem(i);
                if (item == null || !item.isSimilar(prototype)) continue;
                int canTake = Math.min(item.getAmount(), amountLeft);
                if (canTake == 0) continue;
                if (canTake == item.getAmount()) {
                    simInv.setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - canTake);
                    simInv.setItem(i, item);
                }
                amountLeft -= canTake;
                if (amountLeft <= 0) break;
            }
        }
        if (!hasEnoughSpace(simInv.getContents(), selected)) {
            p.sendMessage(CustomCrafting.getInstance().getPrefix() +
                    CustomCrafting.getInstance().getLang().get("not_enough_inventory_space"));
            return;
        }

        for (Map.Entry<ItemStack, Integer> entry : requiredPerCraft.entrySet()) {
            ItemStack prototype = entry.getKey();
            int amountLeft = entry.getValue() * maxCraftCount;

            for (int i = 0; i < p.getInventory().getSize(); i++) {
                ItemStack item = p.getInventory().getItem(i);
                if (item == null || !item.isSimilar(prototype)) continue;

                int canTake = Math.min(item.getAmount(), amountLeft);
                if (canTake == 0) continue;

                if (canTake == item.getAmount()) {
                    p.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - canTake);
                    p.getInventory().setItem(i, item);
                }

                amountLeft -= canTake;
                if (amountLeft <= 0) break;
            }
        }

        for (ItemStack item : selected) {
            p.getInventory().addItem(item);
        }

        p.sendMessage(CustomCrafting.getInstance().getPrefix() +
                CustomCrafting.getInstance().getLang().get("crafting_completed"));
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
        result.serialize(data);
        return data;
    }

    public void deserialize(YamlConfiguration data, String name) {
        this.categoryName = data.getString("name");
        this.name = name;
        for (int i = 0; i < ingredients.length; i++) {
            ingredients[i] = data.getItemStack("Recipe." + name + ".ingredient." + i);
        }
        this.result = new Result(this);
        this.result.deserialize(data);
    }
}
