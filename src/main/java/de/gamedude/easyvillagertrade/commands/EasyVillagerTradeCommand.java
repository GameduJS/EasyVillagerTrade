package de.gamedude.easyvillagertrade.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import de.gamedude.easyvillagertrade.utils.TradingState;
import joptsimple.internal.Strings;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class EasyVillagerTradeCommand implements ClientCommandRegistrationCallback {

    private final EasyVillagerTradeBase modBase;

    public EasyVillagerTradeCommand(EasyVillagerTradeBase modBase) {
        this.modBase = modBase;
    }

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        String command_base = "evt";
        dispatcher.register(literal(command_base)
                .then(literal("select").then(literal("close").executes(this::executeSelectionClosest)).executes(this::executeSelection))
                .then(literal("search")
                        .then(literal("add").then(argument("maxPrice", IntegerArgumentType.integer()).then(argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT)).executes(this::executeAddTradeRequest).then(argument("level", IntegerArgumentType.integer()).executes(this::executeAddTradeRequest)))))

                        .then(literal("remove").then(argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT)).executes(this::executeRemoveTradeRequest)))
                        .then(literal("list").executes(this::executeListTradeRequest)))
                .then(literal("execute").executes(this::executeVillagerTrade))
                .then(literal("stop").executes(ctx -> {
                    modBase.setState(TradingState.INACTIVE);
                    return 1;
                }))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.translatable("evt.command.basic_usage"));
                    return 1;
                }));
    }


    public int executeAddTradeRequest(CommandContext<FabricClientCommandSource> context) {
        Enchantment enchantment = getArgumentReference(context, "enchantment", Enchantment.class);

        int maxPrice = context.getArgument("maxPrice", Integer.class);
        int level = getArgumentOrElse(context, "level", Integer.class, enchantment.getMaxLevel());

        TradeRequest tradeRequest = modBase.getTradeRequestInputHandler().handleCommandInput(enchantment, level, maxPrice);
        modBase.getTradeRequestContainer().addTradeRequest(tradeRequest);

        context.getSource().sendFeedback(Text.translatable("evt.command.add", "§e" + tradeRequest.enchantment().getName(tradeRequest.level()).getString(), "§a" + tradeRequest.maxPrice()));
        return 1;
    }

    public int executeRemoveTradeRequest(CommandContext<FabricClientCommandSource> context) {
        Enchantment enchantment = getArgumentReference(context, "enchantment", Enchantment.class);
        modBase.getTradeRequestContainer().removeTradeRequestByEnchantment(enchantment);

        boolean multipleLevels = enchantment.getMaxLevel() == 1;
        String[] parts = enchantment.getName(1).getString().split(" ");
        String name = Strings.join((multipleLevels) ? parts : Arrays.copyOf(parts, parts.length - 1), " ");

        context.getSource().sendFeedback(Text.translatable("evt.command.remove", "§e" + StringUtils.capitalize(name)));
        return 1;
    }

    public int executeListTradeRequest(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.translatable("evt.command.list.head"));
        modBase.getTradeRequestContainer().getTradeRequests().forEach(offer ->
                context.getSource().sendFeedback(Text.translatable("evt.command.list.body", "§e" + offer.enchantment().getName(offer.level()).getString(), "§a" + offer.maxPrice())));
        return 1;
    }

    public int executeSelection(CommandContext<FabricClientCommandSource> context) {
        this.modBase.setState(TradingState.MODE_SELECTION);
        context.getSource().sendFeedback(Text.translatable("evt.command.selecting"));
        return 1;
    }

    public int executeSelectionClosest(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        int x = this.modBase.getSelectionInterface().selectClosestToPlayer(player);
        switch (x) {
            case 1 -> player.sendMessage(Text.translatable("evt.logic.select.fail_lectern"));
            case 2 -> player.sendMessage(Text.translatable("evt.logic.select.fail_villager"));
            case 0 -> player.sendMessage(Text.translatable("evt.logic.select.success"));
        }
        return 1;
    }

    public int executeVillagerTrade(CommandContext<FabricClientCommandSource> context) {
        this.modBase.setState(TradingState.CHECK_OFFERS);
        context.getSource().sendFeedback(Text.translatable("evt.command.execute"));
        modBase.handleInteractionWithVillager();
        return 1;
    }

    private <T> T getArgumentReference(CommandContext<FabricClientCommandSource> context, String argument, Class<T> argClass) {
        RegistryEntry.Reference<T> reference = context.getArgument(argument, RegistryEntry.Reference.class);
        return reference.value();
    }

    private <T> T getArgumentOrElse(CommandContext<FabricClientCommandSource> context, String argument, Class<T> argumentClass, T orElse) {
        T t;
        try {
            t = context.getArgument(argument, argumentClass);
        } catch ( IllegalArgumentException e ) {
            return orElse;
        }
        return t;
    }
}
