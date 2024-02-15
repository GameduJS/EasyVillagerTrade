package de.gamedude.evt.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import de.gamedude.evt.handler.TradeRequestContainer;
import de.gamedude.evt.utils.TradeRequest;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public record EVTCommand(TradeRequestContainer tradeRequestContainer) implements ClientCommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {

        CommandNode<FabricClientCommandSource> searchNode = literal("search")
                        .then(literal("add").executes(context -> sendFeedback(context, "Use /evt search add <price> <enchantment> <level>"))
                                .then(argument("price", IntegerArgumentType.integer(1, 64))
                                .then(argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT)).executes(this::executeAddSearch)
                                .then(argument("level", IntegerArgumentType.integer(1, 5)).executes(this::executeAddSearch)))))
                        .then(literal("remove").executes(context -> sendFeedback(context, "Use /evt search remove <enchantment>"))
                                .then(argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT)).executes(context -> 1)))
                        .then(literal("list").executes(context -> 1))
                .executes(context -> sendFeedback(context, "Use /evt search <add/remove/list>")).build();

        dispatcher.register(literal("evt")
                        .executes(context -> sendFeedback(context, "Use /evt <start/stop/search>"))

                .then(literal("start").executes(context -> 1))
                .then(literal("stop").executes(context -> 1))

                // SEARCH SUBCOMMAND
                .then(searchNode));
    }

    private int executeAddSearch(CommandContext<?> commandContext) {
        int price = commandContext.getArgument("price", Integer.class);
        Enchantment enchantment = (Enchantment) commandContext.getArgument("enchantment", RegistryEntry.Reference.class).value();
        int level = getArgumentOrElse(commandContext, "level", 1);

        TradeRequest tradeRequest = new TradeRequest(enchantment, level, price);
        tradeRequestContainer.addRequest(tradeRequest);

        return sendFeedback(commandContext, Text.translatable("evt.command.add", "§e" + tradeRequest.enchantment().getName(tradeRequest.level()).getString(), "§a" + tradeRequest.cost()));
    }

    private int getArgumentOrElse(CommandContext<?> commandContext, String name, int orElse) {
        try {
            return commandContext.getArgument(name, Integer.class);
        } catch ( IllegalArgumentException e ) {
            return orElse;
        }
    }

    private int sendFeedback(CommandContext<?> commandContext, Text feedback) {
        if(commandContext.getSource() instanceof FabricClientCommandSource fabricClientCommandSource)
            fabricClientCommandSource.getPlayer().sendMessage(feedback);
        return 1;
    }

    private int sendFeedback(CommandContext<?> commandContext, String feedback) {
        if(commandContext.getSource() instanceof FabricClientCommandSource fabricClientCommandSource)
            fabricClientCommandSource.getPlayer().sendMessage(Text.of(feedback));
        return 1;
    }

}
