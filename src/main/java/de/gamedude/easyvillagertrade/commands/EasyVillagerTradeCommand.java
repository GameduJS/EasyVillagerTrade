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
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
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
                        .then(literal("add").then(argument("maxPrice", IntegerArgumentType.integer(1, 64)).then(argument("enchantment", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT))
                                .executes(context -> executeAddTradeRequest(context, IntegerArgumentType.getInteger(context, "maxPrice"), 1))
                                .then(argument("level", IntegerArgumentType.integer(1, 5)).executes(context -> executeAddTradeRequest(context, IntegerArgumentType.getInteger(context, "maxPrice"), IntegerArgumentType.getInteger(context, "level")))))))

                        .then(literal("remove").then(argument("enchantment", RegistryEntryReferenceArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT))
                                .executes(this::executeRemoveTradeRequest)))

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

    public int executeAddTradeRequest(CommandContext<FabricClientCommandSource> context, int maxPrice, int level) {
        RegistryEntry.Reference<?> reference = context.getArgument("enchantment", RegistryEntry.Reference.class);
        if(!reference.registryKey().isOf(RegistryKeys.ENCHANTMENT))
            return 0;
        Enchantment enchantment = (Enchantment) reference.value();

        TradeRequest tradeRequest = modBase.getTradeRequestInputHandler().handleCommandInput(enchantment, level, maxPrice);
        modBase.getTradeRequestContainer().addTradeRequest(tradeRequest);

        context.getSource().sendFeedback(Text.translatable("evt.command.add", "§e" + Enchantment.getName(tradeRequest.enchantment(), tradeRequest.level()).getString(), "§a" + tradeRequest.maxPrice()));
        return 1;
    }

    @SuppressWarnings("unchecked")
    public int executeRemoveTradeRequest(CommandContext<FabricClientCommandSource> context) {
        RegistryEntry.Reference<?> reference = context.getArgument("enchantment", RegistryEntry.Reference.class);
        if(!(reference.value() instanceof Enchantment enchantment))
            return 0;
        RegistryEntry<Enchantment> enchantmentRegistryEntry = (RegistryEntry<Enchantment>) reference;

        modBase.getTradeRequestContainer().removeTradeRequestByEnchantment(enchantmentRegistryEntry);

        boolean multipleLevels = enchantment.getMaxLevel() == 1;
        String[] parts = Enchantment.getName(enchantmentRegistryEntry, 1).getString().split(" ");
        String name = Strings.join((multipleLevels) ? parts : Arrays.copyOf(parts, parts.length - 1), " ");

        context.getSource().sendFeedback(Text.translatable("evt.command.remove", "§e" + StringUtils.capitalize(name)));
        return 1;
    }

    public int executeListTradeRequest(CommandContext<FabricClientCommandSource> context) {
        context.getSource().sendFeedback(Text.translatable("evt.command.list.head"));
        modBase.getTradeRequestContainer().getTradeRequests().forEach(offer ->
                context.getSource().sendFeedback(Text.translatable("evt.command.list.body", "§e" + Enchantment.getName(offer.enchantment(), offer.level()).getString(), "§a" + offer.maxPrice())));
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
}
