package de.gamedude.evt.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import de.gamedude.evt.handler.SelectionInterface;
import de.gamedude.evt.handler.TradeRequestContainer;
import de.gamedude.evt.handler.TradeRequestParser;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.Script;
import de.gamedude.evt.script.ScriptManager;
import de.gamedude.evt.utils.TradeRequest;
import joptsimple.internal.Strings;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
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

public record EVTCommand(TradeWorkflow tradeWorkflow) implements ClientCommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {

        CommandNode<FabricClientCommandSource> searchNode = literal("search")
                        .then(literal("add").executes(context -> sendFeedback(context, "Use /evt search add <price> <enchantment> <level>"))
                                .then(argument("price", IntegerArgumentType.integer(1, 64))
                                .then(argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT)).executes(this::executeAddSearch)
                                .then(argument("level", IntegerArgumentType.integer(1, 5)).executes(this::executeAddSearch)))))
                        .then(literal("remove").executes(context -> sendFeedback(context, "Use /evt search remove <enchantment>"))
                                .then(argument("enchantment", RegistryEntryArgumentType.registryEntry(registryAccess, RegistryKeys.ENCHANTMENT)).executes(this::executeRemoveSearch)))
                        .then(literal("list").executes(this::executeListSearch))
                .executes(context -> sendFeedback(context, "Use /evt search <add/remove/list>")).build();

        CommandNode<FabricClientCommandSource> selectNode = literal("select")
                .then(literal("close").executes(this::executeCloseSelect))
                .executes(context -> sendFeedback(context, "Manually selecting")).build();


        dispatcher.register(literal("evt")
                        .executes(context -> sendFeedback(context, "Use /evt <start/stop/search>"))

                .then(literal("start").executes(this::executeStart))
                .then(literal("stop").executes(context -> sendFeedback(context, "Stopping....")))

                // SEARCH SUBCOMMAND
                .then(searchNode)
                .then(selectNode));
    }

    private int executeStart(CommandContext<?> commandContext) {
        this.tradeWorkflow.getHandler(ScriptManager.class).getScript().tickScriptType(Script.ScriptType.INIT);
        this.tradeWorkflow.toggle(true);
        return sendFeedback(commandContext, "Starting the search...");
    }

    private int executeAddSearch(CommandContext<?> commandContext) {
        Enchantment enchantment = (Enchantment) commandContext.getArgument("enchantment", RegistryEntry.Reference.class).value();
        int level = Math.min(enchantment.getMaxLevel(), getArgumentOrElse(commandContext, "level", 1));
        int price = commandContext.getArgument("price", Integer.class);

        TradeRequest tradeRequest = tradeWorkflow.getHandler(TradeRequestParser.class).parse(enchantment, level, price);
        tradeWorkflow.getHandler(TradeRequestContainer.class).addRequest(tradeRequest);

        return sendFeedback(commandContext, Text.translatable("evt.command.add", "§e" + tradeRequest.enchantment().getName(tradeRequest.level()).getString(), "§a" + tradeRequest.cost()));
    }

    private int executeRemoveSearch(CommandContext<?> commandContext) {
        Enchantment enchantment = (Enchantment) commandContext.getArgument("enchantment", RegistryEntry.Reference.class).value();
        tradeWorkflow.getHandler(TradeRequestContainer.class).removeRequestByEnchantment(enchantment);

        String[] parts = enchantment.getName(1).getString().split(" ");
        String name = Strings.join((enchantment.getMaxLevel() == 1) ? parts : Arrays.copyOf(parts, parts.length - 1), " ");
        return sendFeedback(commandContext, Text.translatable("evt.command.remove", "§e" + StringUtils.capitalize(name)));
    }

    private int executeListSearch(CommandContext<?> commandContext) {
       sendFeedback(commandContext, Text.translatable("evt.command.list.head"));
        tradeWorkflow.getHandler(TradeRequestContainer.class).getRequests().forEach(offer ->
                sendFeedback(commandContext, Text.translatable("evt.command.list.body", "§e" + offer.enchantment().getName(offer.level()).getString(), "§a" + offer.cost())));
        return 1;
    }

    private int executeCloseSelect(CommandContext<?> commandContext) {
        ClientPlayerEntity player  = MinecraftClient.getInstance().player;
        switch (tradeWorkflow.getHandler(SelectionInterface.class).selectClosestToPlayer(player)) {
            case 1 -> player.sendMessage(Text.translatable("evt.logic.select.fail_lectern"));
            case 2 -> player.sendMessage(Text.translatable("evt.logic.select.fail_villager"));
            case 0 -> player.sendMessage(Text.translatable("evt.logic.select.success"));
        }
        return 1;
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
