package com.darksoldier1404.dpcc.events;

import com.darksoldier1404.dpcc.CustomCrafting;
import com.darksoldier1404.dpcc.functions.DPCCFunction;
import com.darksoldier1404.dpcc.obj.Category;
import com.darksoldier1404.dpcc.obj.Recipe;
import com.darksoldier1404.dpcc.obj.Result;
import com.darksoldier1404.dppc.api.inventory.DInventory;
import com.darksoldier1404.dppc.events.dinventory.DInventoryClickEvent;
import com.darksoldier1404.dppc.events.dinventory.DInventoryCloseEvent;
import com.darksoldier1404.dppc.utils.NBT;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;


public class DPCCEvent implements Listener {
    private final CustomCrafting plugin = CustomCrafting.getInstance();

    @EventHandler
    public void onInventoryClose(DInventoryCloseEvent e) {
        DInventory inv = e.getDInventory();
        Player p = (Player) e.getPlayer();
        if (inv.isValidHandler(plugin)) {
            if (inv.isValidChannel(201)) { // recipe item edit
                DPCCFunction.saveRecipeItem(p, inv);
                return;
            }
            if (inv.isValidChannel(300)) {
                Result r = (Result) inv.getObj();
                Bukkit.getScheduler().runTaskLater(plugin, () -> r.getParentRecipe().openRecipeCraft(p), 1L);
                return;
            }
            if (inv.isValidChannel(301)) { // result item edit
                DPCCFunction.saveResultItem(p, inv);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(DInventoryClickEvent e) {
        DInventory inv = e.getDInventory();
        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();
        if (inv.isValidHandler(plugin)) {
            if (inv.isValidChannel(0)) { // recipe list from category
                if (item == null) return;
                e.setCancelled(true);
                if (NBT.hasTagKey(item, "dpcc_recipe_name")) {
                    String recipeName = NBT.getStringTag(item, "dpcc_recipe_name");
                    Category c = (Category) inv.getObj();
                    DPCCFunction.openRecipeCraft(p, c, recipeName);
                    return;
                }
            }
            if (inv.isValidChannel(100)) { // recipe crafting
                e.setCancelled(true);
                Recipe r = (Recipe) inv.getObj();
                if (NBT.hasTagKey(item, "dpcc_confirm")) {
                    // craft and give result
                    r.craft(p);
                    return;
                }
                if (e.getRawSlot() == 10) {
                    r.getResult().preview(p);
                    return;
                }
            }
            if (inv.isValidChannel(300)) { // preview result item and chances
                e.setCancelled(true);
                return;
            }
            if (inv.isValidChannel(302)) { // weight setting
                if (item == null) return;
                if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER)
                    return;
                e.setCancelled(true);
                DPCCFunction.editResultWeightWithChat(p, e.getSlot());
                p.sendMessage(plugin.getPrefix() + plugin.getLang().get("enter_weight_chat"));
                p.closeInventory();
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (CustomCrafting.currentEditingWeight.containsKey(p.getUniqueId())) {
            e.setCancelled(true);
            DPCCFunction.setResultWeightFromChat(p, e.getMessage());
        }
    }
}
