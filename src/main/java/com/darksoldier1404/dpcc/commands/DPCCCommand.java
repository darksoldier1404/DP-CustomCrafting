package com.darksoldier1404.dpcc.commands;

import com.darksoldier1404.dpcc.CustomCrafting;
import com.darksoldier1404.dpcc.functions.DPCCFunction;
import com.darksoldier1404.dppc.builder.command.ArgumentIndex;
import com.darksoldier1404.dppc.builder.command.ArgumentType;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiFunction;

public class DPCCCommand {
    private static final CustomCrafting plugin = CustomCrafting.getInstance();

    public static void init() {
        CommandBuilder builder = new CommandBuilder(plugin);
        builder.beginSubCommand("createcategory", plugin.getLang().get("command_createcategory_description"))
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING)
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String name = args.getString(ArgumentIndex.ARG_0);
                    DPCCFunction.createCategory(p, name);
                    return true;
                });
        builder.beginSubCommand("createrecipe", plugin.getLang().get("command_createrecipe_description"))
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING, getRecipeListBiFunction())
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.createRecipe(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("recipeitem", plugin.getLang().get("command_recipeitem_description"))
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING, getRecipeListBiFunction())
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.openRecipeItemSettingInventory(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("setresultitem", plugin.getLang().get("command_setresultitem_description"))
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING, getRecipeListBiFunction())
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.openResultItemSettingInventory(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("setresultamount", plugin.getLang().get("command_setresultamount_description"))
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING, getRecipeListBiFunction())
                .withArgument(ArgumentIndex.ARG_2, ArgumentType.INTEGER)
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    int amount = args.getInteger(ArgumentIndex.ARG_2);
                    DPCCFunction.setResultAmount(p, categoryName, recipeName, amount);
                    return true;
                });
        builder.beginSubCommand("setresultweight", plugin.getLang().get("command_setresultweight_description"))
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING, getRecipeListBiFunction())
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.openResultWeightSettingInventory(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("opencategory", plugin.getLang().get("command_opencategory_description"))
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    DPCCFunction.openCategoryInventory(p, categoryName);
                    return true;
                });
        builder.beginSubCommand("reload", plugin.getLang().get("command_reload_description"))
                .withPermission("dpcc.admin")
                .executes((p, args) -> {
                    plugin.reload();
                    plugin.showChanceInPreview = plugin.getConfig().getBoolean("Settings.showChanceInPreview");
                    p.sendMessage(plugin.getPrefix() + plugin.getLang().get("config_reloaded"));
                    return true;
                });

        builder.build("dpcc");
    }

    private static @NotNull BiFunction<Player, String[], List<String>> getRecipeListBiFunction() {
        return (p, args) -> {
            if (plugin.getData().containsKey(args[1])) {
                return plugin.getData().get(args[1]).getRecipes().keySet().stream().toList();
            }
            return null;
        };
    }
}
