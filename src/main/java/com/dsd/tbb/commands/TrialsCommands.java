package com.dsd.tbb.commands;

import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.TBBLogger;
import com.dsd.tbb.util.EnumTypes;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

import java.util.List;
import java.util.function.Supplier;

public class TrialsCommands {
    private static final List<String> mainOptions = ConfigManager.getInstance().getTrialsConfig().getCommandList();
    private static final List<String> setOptions = ConfigManager.getInstance().getTrialsConfig().getSetCommandList();
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(TrialsCommands.buildToggleCommand());
        dispatcher.register(TrialsCommands.buildSetCommand());
        TBBLogger.getInstance().info("TrialsCommands","Registered Command");
    }

    /************************************ COMMAND EXECUTOR METHODS ***********************************/
    public static int toggleCommand(CommandContext context){
        int didSucceed = 1; //0 = failed, 1 = success
        Style style = Style.EMPTY.withColor(TextColor.parseColor("Green"));
        StringBuilder sb = new StringBuilder();
        String option = (String) context.getArgument("option", String.class);
        CommandSourceStack cs = (CommandSourceStack) context.getSource();
        EnumTypes.ModConfigOption configOption = EnumTypes.ModConfigOption.fromOptionName(option);

        sb.append(ConfigManager.getInstance().toggleMainConfigOption(configOption));

        TBBLogger.getInstance().debug("toggleComand",sb.toString());
        Supplier<Component> componentSupplier = () -> Component.literal(sb.toString()).setStyle(style);
        cs.sendSuccess(componentSupplier.get(),true);
        return didSucceed;
    }

    public static int setCommand(CommandContext context) {
        int didSucceed = 1;
        Style style = Style.EMPTY.withColor(TextColor.parseColor("Green"));
        StringBuilder sb = new StringBuilder();
        String option = (String) context.getArgument("option", String.class);
        int value = (int) context.getArgument("value", Integer.class);
        CommandSourceStack cs = (CommandSourceStack) context.getSource();
        EnumTypes.ModConfigOption configOption = EnumTypes.ModConfigOption.fromOptionName(option);

        sb.append(ConfigManager.getInstance().setIntConfigOption(configOption, value));

        TBBLogger.getInstance().debug("setCommand",sb.toString());
        Supplier<Component> componentSupplier = () -> Component.literal(sb.toString()).setStyle(style);
        cs.sendSuccess(componentSupplier.get(), true);
        return didSucceed;
    }

    /************************LITERAL BUILDER FOR COMMANDS*****************************************/
    public static LiteralArgumentBuilder<CommandSourceStack> buildToggleCommand() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("trialsbybaby")
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("toggle")
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("option", StringArgumentType.string())
                                .suggests((context, builder) ->{
                                    for(String option : mainOptions){
                                        builder.suggest(option);
                                    }
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    return toggleCommand(context);
                                    // Handle toggle command logic
                                })));

    }

    public static LiteralArgumentBuilder<CommandSourceStack> buildSetCommand() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("trialsbybaby")
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("option", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    for (String option : setOptions) {
                                        builder.suggest(option);
                                    }
                                    return builder.buildFuture();
                                })
                                .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("value", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            return setCommand(context);
                                        })
                                )
                        )
                );
    }

}
