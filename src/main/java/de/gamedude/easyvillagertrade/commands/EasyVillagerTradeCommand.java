package de.gamedude.easyvillagertrade.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import de.gamedude.easyvillagertrade.utils.TradingState;
import joptsimple.internal.Strings;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;


public class EasyVillagerTradeCommand {

    private final EasyVillagerTradeBase modBase;

    public EasyVillagerTradeCommand(EasyVillagerTradeBase modBase) {
        this.modBase = modBase;
    }

    public  void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandCtx) {
        String command_base = "evt";
        dispatcher.register(literal(command_base)
                .then(literal("select").then(literal("close").executes(this::executeSelectionClosest)).executes(this::executeSelection))
                .then(literal("search")

                .then(literal("add").then(argument("maxPrice", IntegerArgumentType.integer(1, 64)).then(argument("enchantment", ResourceArgument.resource(commandCtx, Registries.ENCHANTMENT))
                                .executes(context -> executeAddTradeRequest(context, IntegerArgumentType.getInteger(context, "maxPrice"), 1))
                                .then(argument("level", IntegerArgumentType.integer(1, 5)).executes(context -> executeAddTradeRequest(context, IntegerArgumentType.getInteger(context, "maxPrice"), IntegerArgumentType.getInteger(context, "level")))))))

                .then(literal("remove").then(argument("enchantment", ResourceArgument.resource(commandCtx, Registries.ENCHANTMENT))
                                .executes(this::executeRemoveTradeRequest)))

                .then(literal("list").executes(this::executeListTradeRequest)))
                .then(literal("execute").executes(this::executeVillagerTrade))
                .then(literal("stop").executes(ctx -> {
                    modBase.setState(TradingState.INACTIVE);
                    return 1;
                }))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Component.translatable("evt.command.basic_usage"));
                    return 1;
                }));
    }

    private int executeAddTradeRequest(CommandContext<FabricClientCommandSource> context, int maxPrice, int level) {
        Holder.Reference<Enchantment> enchantmentReference = context.getArgument("enchantment", Holder.Reference.class);

        TradeRequest tradeRequest = modBase.getTradeRequestInputHandler().parseCommandInput(enchantmentReference, level, maxPrice);
        modBase.getTradeRequestContainer().addTradeRequest(tradeRequest);

        context.getSource().sendFeedback(Component.translatable("evt.command.add", "§e" + tradeRequest.getNameEnchantment().getString(), "§a" + tradeRequest.maxPrice()));
        return 1;
    }

    @SuppressWarnings("unchecked")
    private int executeRemoveTradeRequest(CommandContext<FabricClientCommandSource> context) {
        Holder.Reference<Enchantment> reference = context.getArgument("enchantment", Holder.Reference.class);

        modBase.getTradeRequestContainer().removeTradeRequestByEnchantment(reference);

        boolean multipleLevels = reference.value().getMaxLevel() == 1;
        String[] parts = Enchantment.getFullname(reference, 1).getString().split(" ");
        String name = Strings.join((multipleLevels) ? parts : Arrays.copyOf(parts, parts.length - 1), " ");

        context.getSource().sendFeedback(Component.translatable("evt.command.remove", "§e" + StringUtils.capitalize(name)));
        return 1;
    }

    public int executeListTradeRequest(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Component.translatable("evt.command.list.head"));
        modBase.getTradeRequestContainer().getTradeRequests().forEach(offer ->
                context.getSource().sendFeedback(Component.translatable("evt.command.list.body", "§e" + offer.getNameEnchantment().getString(), "§a" + offer.maxPrice())));
        return 1;
    }

    public int executeSelection(CommandContext<FabricClientCommandSource> context) {
        this.modBase.setState(TradingState.MODE_SELECTION);
        context.getSource().sendFeedback(Component.translatable("evt.command.selecting"));
        return 1;
    }

    public int executeSelectionClosest(CommandContext<FabricClientCommandSource> context) {
        Player player = context.getSource().getPlayer();
        int x = modBase.getSelectionInterface().selectClosestToPlayer(player);
        switch (x) {
            case 1 -> player.sendSystemMessage(Component.translatable("evt.logic.select.fail_lectern"));
            case 2 -> player.sendSystemMessage(Component.translatable("evt.logic.select.fail_villager"));
            case 0 -> player.sendSystemMessage(Component.translatable("evt.logic.select.success"));
        }
        return 1;
    }

    public int executeVillagerTrade(CommandContext<FabricClientCommandSource> context) {
        this.modBase.setState(TradingState.CHECK_OFFERS);

        if(modBase.getSelectionInterface().getVillager() == null || modBase.getSelectionInterface().getLecternPos() == null) {
            context.getSource().sendFeedback(Component.translatable("evt.command.not_selected"));
            return 1;
        }

        context.getSource().sendFeedback(Component.translatable("evt.command.execute"));
        modBase.handleInteractionWithVillager();
        return 1;
    }

}
