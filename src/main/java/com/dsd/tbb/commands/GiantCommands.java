package com.dsd.tbb.commands;

import com.dsd.tbb.managers.ConfigManager;
import com.dsd.tbb.util.EnumTypes;
import com.dsd.tbb.util.TBBLogger;
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

public class GiantCommands {
    private static final List<String> setCommandList = ConfigManager.getInstance().getGiantConfig().getSetCommandList();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(GiantCommands.buildSetCommand());
        TBBLogger.getInstance().info("GiantCommands", "Registered Giant Command");
    }

    public static LiteralArgumentBuilder<CommandSourceStack> buildSetCommand() {
        return LiteralArgumentBuilder.<CommandSourceStack>literal("tbbgiant")
                .then(LiteralArgumentBuilder.<CommandSourceStack>literal("set")
                        .then(RequiredArgumentBuilder.<CommandSourceStack, String>argument("option", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    for (String option : setCommandList) {
                                        builder.suggest(option);
                                    }
                                    return builder.buildFuture();
                                })
                                .then(RequiredArgumentBuilder.<CommandSourceStack, Integer>argument("value", IntegerArgumentType.integer())
                                        .executes(context -> setGiantCommand(context))
                                )
                        )
                );
    }

    public static int setGiantCommand(CommandContext<CommandSourceStack> context) {
        int didSucceed = 1;
        Style style = Style.EMPTY.withColor(TextColor.parseColor("Green"));
        StringBuilder sb = new StringBuilder();
        String option = context.getArgument("option", String.class);
        int value = IntegerArgumentType.getInteger(context, "value");
        CommandSourceStack cs = context.getSource();
        // Assuming EnumTypes.ModConfigOption can handle GiantConfig options
        EnumTypes.ModConfigOption configOption = EnumTypes.ModConfigOption.fromOptionName(option);

        // Assuming ConfigManager can handle setting GiantConfig options similarly to TrialsConfig
        assert configOption != null;
        sb.append(ConfigManager.getInstance().setGiantConfigOption(configOption, value));

        //TBBLogger.getInstance().debug("setGiantCommand", sb.toString());
        Supplier<Component> componentSupplier = () -> Component.literal(sb.toString()).setStyle(style);
        cs.sendSuccess(componentSupplier.get(), true);
        return didSucceed;
    }
}
