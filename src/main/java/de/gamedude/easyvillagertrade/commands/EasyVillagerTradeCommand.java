package de.gamedude.easyvillagertrade.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import de.gamedude.easyvillagertrade.core.EasyVillagerTradeBase;
import de.gamedude.easyvillagertrade.utils.TradeRequest;
import de.gamedude.easyvillagertrade.utils.TradingState;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EnchantmentArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;

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
                .then(literal("select").executes(this::executeSelection))
                .then(literal("search")
                        .then(literal("add").then(argument("maxPrice", IntegerArgumentType.integer()).then(argument("enchantment", EnchantmentArgumentType.enchantment()).executes(this::executeAddTradeRequest).then(argument("level", IntegerArgumentType.integer()).executes(this::executeAddTradeRequest)))))

                        .then(literal("remove").then(argument("enchantment", EnchantmentArgumentType.enchantment()).executes(this::executeRemoveTradeRequest)))
                        .then(literal("list").executes(this::executeListTradeRequest)))
                .then(literal("execute").executes(this::executeVillagerTrade))
                .then(literal("stop").executes(ctx -> {
                    modBase.setState(TradingState.INACTIVE);
                    return 1;
                }))
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.of("Please use /evt <select/search/execute/stop>"));
                    return 1;
                }));
    }


    public int executeAddTradeRequest(CommandContext<FabricClientCommandSource> context) {
        Enchantment enchantment = context.getArgument("enchantment", Enchantment.class);
        int maxPrice = context.getArgument("maxPrice", Integer.class);
        int level = getArgumentOrElse(context, "level", Integer.class, enchantment.getMaxLevel());

        TradeRequest tradeRequest = modBase.getTradeRequestInputHandler().handleCommandInput(enchantment, level, maxPrice);
        modBase.getTradeRequestContainer().addTradeRequest(tradeRequest);

        context.getSource().sendFeedback(Text.of("§8| §7Added search query for §e" + tradeRequest.enchantment().getName(tradeRequest.level()).getString() + "§7 for a maximum of§a " + tradeRequest.maxPrice() + " Emeralds"));
        return 1;
    }

    public int executeRemoveTradeRequest(CommandContext<FabricClientCommandSource> context) {
        Enchantment enchantment = context.getArgument("enchantment", Enchantment.class);
        modBase.getTradeRequestContainer().removeTradeRequestByEnchantment(enchantment);

        context.getSource().sendFeedback(Text.of("§8| §7Removed any search queries for §e" + enchantment.getTranslationKey().split("\\.")[2] + "§7 enchantment"));
        return 1;
    }

    public int executeListTradeRequest(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.of("§8| §7List of all search queries: "));
        modBase.getTradeRequestContainer().getTradeRequests().forEach(offer ->
                context.getSource().sendFeedback(Text.of("§7- §e" + offer.enchantment().getName(offer.level()).getString() + "§7 for a maximum of §a" + offer.maxPrice() + " Emeralds")));
        return 1;
    }

    public int executeSelection(CommandContext<FabricClientCommandSource> context) {
        this.modBase.setState(TradingState.MODE_SELECTION);
        context.getSource().sendFeedback(Text.of("§8| §7Select the villager & lectern that should be handled"));
        return 1;
    }

    public int executeVillagerTrade(CommandContext<FabricClientCommandSource> context) {
        this.modBase.setState(TradingState.CHECK_OFFERS);
        context.getSource().sendFeedback(Text.of("§8| §7Executing all search queries"));
        modBase.handleInteractionWithVillager();
        return 1;
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
