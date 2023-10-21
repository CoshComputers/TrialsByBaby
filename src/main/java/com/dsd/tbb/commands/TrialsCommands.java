package com.dsd.tbb.commands;

import com.dsd.tbb.util.ConfigManager;
import com.dsd.tbb.util.CustomLogger;
import com.dsd.tbb.util.EnumTypes;
import com.mojang.brigadier.CommandDispatcher;
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
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(TrialsCommands.build());
        CustomLogger.getInstance().debug("Registered Command");
    }
    public static int toggleCommand(CommandContext context){
        int didSucceed = 1; //0 = failed, 1 = success
        Style style = Style.EMPTY.withColor(TextColor.parseColor("Green"));
        StringBuilder sb = new StringBuilder();
        String option = (String) context.getArgument("option", String.class);
        CommandSourceStack cs = (CommandSourceStack) context.getSource();
        EnumTypes.ModConfigOption configOption = EnumTypes.ModConfigOption.fromOptionName(option);
        sb.append(ConfigManager.getInstance().toggleMainConfigOption(configOption));

        CustomLogger.getInstance().debug(sb.toString());
        Supplier<Component> componentSupplier = () -> Component.literal(sb.toString()).setStyle(style);
        cs.sendSuccess(componentSupplier.get(),true);
        return didSucceed;
    }
    /************************LITERAL BUILDER FOR COMMANDS*****************************************/
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
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


}
