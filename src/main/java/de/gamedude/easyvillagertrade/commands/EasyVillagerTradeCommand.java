package de.gamedude.easyvillagertrade.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.gamedude.easyvillagertrade.core.SelectionInterface;
import de.gamedude.easyvillagertrade.core.TradeRequestContainer;
import de.gamedude.easyvillagertrade.core.TradeRequestInputHandler;
import de.gamedude.easyvillagertrade.core.TradeWorkflowHandler;
import de.gamedude.easyvillagertrade.core.autowalk.VillagerHubEngine;
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
import net.minecraft.util.math.Vec2f;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class EasyVillagerTradeCommand implements ClientCommandRegistrationCallback {

    private final TradeWorkflowHandler tradeWorkflowHandler;
    private final VillagerHubEngine villagerHubEngine;
    private final TradeRequestInputHandler tradeRequestInputHandler;
    private final SelectionInterface selectionInterface;
    private final TradeRequestContainer tradeRequestContainer;

    public EasyVillagerTradeCommand(TradeWorkflowHandler modBase) {
        this.tradeWorkflowHandler = modBase;
        this.villagerHubEngine = tradeWorkflowHandler.getHandler(VillagerHubEngine.class);
        this.tradeRequestInputHandler = tradeWorkflowHandler.getHandler(TradeRequestInputHandler.class);
        this.selectionInterface = tradeWorkflowHandler.getHandler(SelectionInterface.class);
        this.tradeRequestContainer = tradeWorkflowHandler.getHandler(TradeRequestContainer.class);
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
                    tradeWorkflowHandler.setState(TradingState.INACTIVE);
                    return 1;
                }))
                .then(literal("test").then(argument("dx", IntegerArgumentType.integer()).then(argument("dz", IntegerArgumentType.integer()).executes(context -> {
                    villagerHubEngine. setDistance(new Vec2f(context.getArgument("dx", Integer.class), context.getArgument("dz", Integer.class)));
                    return 1;
                }))))
                .then(literal("test2").executes(context -> {
                    villagerHubEngine.onWalk(context.getSource().getPlayer());
                    return 1;
                }))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.translatable("evt.command.basic_usage"));
                    return 1;
                }));
    }


    public int executeAddTradeRequest(CommandContext<FabricClientCommandSource> context) {
        Enchantment enchantment = getEnchantment(context);

        int maxPrice = context.getArgument("maxPrice", Integer.class);
        int level = getArgumentOrElse(context, enchantment.getMaxLevel());

        TradeRequest tradeRequest = tradeRequestInputHandler.handleCommandInput(enchantment, level, maxPrice);
        tradeRequestContainer.addTradeRequest(tradeRequest);

        context.getSource().sendFeedback(Text.translatable("evt.command.add", "§e" + tradeRequest.enchantment().getName(tradeRequest.level()).getString(), "§a" + tradeRequest.maxPrice()));
        return 1;
    }

    public int executeRemoveTradeRequest(CommandContext<FabricClientCommandSource> context) {
        Enchantment enchantment = getEnchantment(context);
        tradeRequestContainer.removeTradeRequestByEnchantment(enchantment);

        boolean multipleLevels = enchantment.getMaxLevel() == 1;
        String[] parts = enchantment.getName(1).getString().split(" ");
        String name = Strings.join((multipleLevels) ? parts : Arrays.copyOf(parts, parts.length - 1), " ");

        context.getSource().sendFeedback(Text.translatable("evt.command.remove", "§e" + StringUtils.capitalize(name)));
        return 1;
    }

    public int executeListTradeRequest(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.translatable("evt.command.list.head"));
        tradeRequestContainer.getTradeRequests().forEach(offer ->
                context.getSource().sendFeedback(Text.translatable("evt.command.list.body", "§e" + offer.enchantment().getName(offer.level()).getString(), "§a" + offer.maxPrice())));
        return 1;
    }

    public int executeSelection(CommandContext<FabricClientCommandSource> context) {
        this.tradeWorkflowHandler.setState(TradingState.MODE_SELECTION);
        context.getSource().sendFeedback(Text.translatable("evt.command.selecting"));

        return 1;
    }

    public int executeSelectionClosest(CommandContext<FabricClientCommandSource> context) {
        ClientPlayerEntity player = context.getSource().getPlayer();
        int x = this.selectionInterface.selectClosestToPlayer(player);
        switch (x) {
            case 1 -> player.sendMessage(Text.translatable("evt.logic.select.fail_lectern"));
            case 2 -> player.sendMessage(Text.translatable("evt.logic.select.fail_villager"));
            case 0 -> player.sendMessage(Text.translatable("evt.logic.select.success"));
        }
        return 1;
    }

    public int executeVillagerTrade(CommandContext<FabricClientCommandSource> context) {
        this.tradeWorkflowHandler.setState(TradingState.CHECK_OFFERS);
        context.getSource().sendFeedback(Text.translatable("evt.command.execute"));
        tradeWorkflowHandler.handleInteractionWithVillager();
        villagerHubEngine.setStartPosition(context.getSource().getPlayer().getPos());
        return 1;
    }

    @SuppressWarnings("unchecked")
    private Enchantment getEnchantment(CommandContext<FabricClientCommandSource> context) {
        RegistryEntry.Reference<Enchantment> reference = context.getArgument("enchantment", RegistryEntry.Reference.class);
        return reference.value();
    }

    private int getArgumentOrElse(CommandContext<FabricClientCommandSource> context, int orElse) {
        try {
            return context.getArgument("level", Integer.class);
        } catch (IllegalArgumentException e) {
            return orElse;
        }
    }
}
