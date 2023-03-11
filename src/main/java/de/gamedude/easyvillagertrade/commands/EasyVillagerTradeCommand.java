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
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

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
                        .then(literal("add").then(argument("maxPrice", IntegerArgumentType.integer()).then(argument("enchantment", EnchantmentArgumentType.enchantment()).executes(ctx -> executeAddTradeRequest(ctx, true)).then(argument("level", IntegerArgumentType.integer()).executes(ctx -> executeAddTradeRequest(ctx, false))))))

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


    public int executeAddTradeRequest(CommandContext<FabricClientCommandSource> context, boolean beMaxLevel) {
        Enchantment enchantment = context.getArgument("enchantment", Enchantment.class);
        Registry<Enchantment> enchantmentRegistry = Registry.ENCHANTMENT;
        int finalLevel = enchantmentRegistry.get(new Identifier(enchantment.getTranslationKey().split("\\.")[2])).getMaxLevel();
        int maxPrice = context.getArgument("maxPrice", Integer.class);

        if (!beMaxLevel) {
            int requestedLevel = context.getArgument("level", Integer.class);
            if (requestedLevel <= 0)
                requestedLevel = 1;
            if (requestedLevel < finalLevel)
                finalLevel = requestedLevel;
        }

        modBase.getTradeRequestContainer().addTradeRequest(new TradeRequest(enchantment, finalLevel, maxPrice));
        context.getSource().sendFeedback(Text.of("§8| §7Added search query for §e" + enchantment.getName(finalLevel).getString() + "§7 for a maximum of§a " + maxPrice + " Emeralds"));
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
}
