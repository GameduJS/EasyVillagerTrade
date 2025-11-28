package de.gamedude.evt.commands;

import com.mojang.brigadier.CommandDispatcher;
import de.gamedude.evt.automation.State;
import de.gamedude.evt.automation.states.LookState;
import de.gamedude.evt.automation.states.MoveState;
import de.gamedude.evt.automation.states.SelectState;
import de.gamedude.evt.handler.TradeWorkflow;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class AutomationTestCommand implements ClientCommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("automation")

                .then(literal("walk")
                        .executes(context -> {
                            TradeWorkflow.testState = new MoveState(3, -1);
                            TradeWorkflow.testState.initState();
                            ClientPlayerEntity player = context.getSource().getPlayer();
                            player.sendMessage(Text.of("Set MoveState 3, -1"));
                            return 0;
                        }))
                .then(literal("look").executes(context -> {
                    TradeWorkflow.testState = new LookState(3d, -1d);
                    TradeWorkflow.testState.initState();
                    ClientPlayerEntity player = context.getSource().getPlayer();
                    player.sendMessage(Text.of("Set LookState yaw 3, pitch -1"));
                    return 0;
                }))
                .then(literal("select").executes(context -> {
                    TradeWorkflow.testState = new SelectState();
                    TradeWorkflow.testState.initState();
                    ClientPlayerEntity player = context.getSource().getPlayer();
                    player.sendMessage(Text.of("Set SelectState"));
                    return 0;
                }))
                .then(literal("process").executes(context -> {
                    LookState lookState = new LookState(2d, 2d);
                    MoveState moveState = new MoveState(2d, 2d);
                    SelectState selectState = new SelectState();
                    var l = List.of(lookState, moveState, selectState);
                    l.forEach(State::initState);

                    TradeWorkflow.STATES.addAll(l);

                    ClientPlayerEntity player = context.getSource().getPlayer();
                    player.sendMessage(Text.of("PROCESS"));
                    return 0;
                }))

        );
    }
}
