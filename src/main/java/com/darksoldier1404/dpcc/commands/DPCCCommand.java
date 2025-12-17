package com.darksoldier1404.dpcc.commands;

import com.darksoldier1404.dpcc.CustomCrafting;
import com.darksoldier1404.dpcc.functions.DPCCFunction;
import com.darksoldier1404.dppc.builder.command.ArgumentIndex;
import com.darksoldier1404.dppc.builder.command.ArgumentType;
import com.darksoldier1404.dppc.builder.command.CommandBuilder;

public class DPCCCommand {
    private static final CustomCrafting plugin = CustomCrafting.getInstance();

    public static void init() {
        CommandBuilder builder = new CommandBuilder(plugin);
        builder.beginSubCommand("createcategory", "/dpcc createcategory <categoryName>")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING)
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String name = args.getString(ArgumentIndex.ARG_0);
                    DPCCFunction.createCategory(p, name);
                    return true;
                });
        builder.beginSubCommand("createrecipe", "/dpcc createrecipe <categoryName> <recipeName>")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING)
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.createRecipe(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("recipeitem", "/dpcc recipeitem <categoryName> <recipeName>")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING)
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.openRecipeItemSettingInventory(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("setresultitem", "/dpcc setresultitem <categoryName> <recipeName>")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING)
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.openResultItemSettingInventory(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("setresultamount", "/dpcc setresultamount <categoryName> <recipeName> <amount>")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING)
                .withArgument(ArgumentIndex.ARG_2, ArgumentType.INTEGER)
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    int amount = args.getInteger(ArgumentIndex.ARG_2);
                    DPCCFunction.setResultAmount(p, categoryName, recipeName, amount);
                    return true;
                });
        builder.beginSubCommand("setresultweight", "/dpcc setresultweight <categoryName> <recipeName>")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .withArgument(ArgumentIndex.ARG_1, ArgumentType.STRING)
                .withPermission("dpcc.admin")
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    String recipeName = args.getString(ArgumentIndex.ARG_1);
                    DPCCFunction.openResultWeightSettingInventory(p, categoryName, recipeName);
                    return true;
                });
        builder.beginSubCommand("opencategory", "/dpcc opencategory <categoryName>")
                .withArgument(ArgumentIndex.ARG_0, ArgumentType.STRING, plugin.getData().keySet())
                .executesPlayer((p, args) -> {
                    String categoryName = args.getString(ArgumentIndex.ARG_0);
                    DPCCFunction.openCategoryInventory(p, categoryName);
                    return true;
                });
        builder.beginSubCommand("reload", "/dpcc reload")
                .withPermission("dpcc.admin")
                .executes((p, args) -> {
                    plugin.reload();
                    plugin.showChanceInPreview = plugin.getConfig().getBoolean("Settings.showChanceInPreview");
                    p.sendMessage(plugin.getPrefix() + "config reloaded.");
                    return true;
                });

        builder.build("dpcc");
    }
}
