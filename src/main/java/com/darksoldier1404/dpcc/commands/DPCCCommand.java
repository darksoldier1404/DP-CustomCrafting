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
        builder.beginSubCommand("createcategory", "/dpcc createcategory <categoryName> - create a new category")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING)
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String name = args.getString(ArgumentIndex.ARG_0);
                    DPCCFunction.createCategory(p, name);
                    return true;
                });
        builder.beginSubCommand("createrecipe", "/dpcc createrecipe <categoryName> <recipeName> - create a new recipe")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING, getRecipeListBiFunction())
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.createRecipe(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("recipeitem", "/dpcc recipeitem <categoryName> <recipeName> - set recipe items")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING, getRecipeListBiFunction())
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.openRecipeItemSettingInventory(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("setresultitem", "/dpcc setresultitem <categoryName> <recipeName> - set result item")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING, getRecipeListBiFunction())
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.openResultItemSettingInventory(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("setresultamount", "/dpcc setresultamount <categoryName> <recipeName> <amount> - set result amount")
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
        builder.beginSubCommand("setresultweight", "/dpcc setresultweight <categoryName> <recipeName> - set result weight")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING, getRecipeListBiFunction())
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.openResultWeightSettingInventory(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("opencategory", "/dpcc opencategory <categoryName> - open category inventory")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    DPCCFunction.openCategoryInventory(p, categoryName);
                    return true;
                });
        builder.beginSubCommand("reload", "/dpcc reload - reload the config")
                .withPermission("dpcc.admin")
                .executes((p, args) -> {
                    plugin.reload();
                    plugin.showChanceInPreview = plugin.getConfig().getBoolean("Settings.showChanceInPreview");
                    p.sendMessage(plugin.getPrefix() + "config reloaded.");
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
