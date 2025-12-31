package de.gamedude.evt.commands;

import com.mojang.brigadier.CommandDispatcher;
import de.gamedude.evt.automation.State;
import de.gamedude.evt.automation.states.*;
import de.gamedude.evt.handler.TradeRequestContainer;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.script.ScriptManager;
import de.gamedude.evt.utils.TradeRequest;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.text.Text;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class AutomationTestCommand implements ClientCommandRegistrationCallback {

    private final TradeWorkflow tradeWorkflow = TradeWorkflow.INSTANCE;

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
                .then(literal("selintchebuy").executes(context -> {
                    TradeWorkflow.INSTANCE.getHandler(TradeRequestContainer.class)
                            .addRequest(new TradeRequest(Enchantments.MENDING, 1, 30));
                    SelectState  selectState = new SelectState();
                    InteractState interactState = new InteractState();
                    CheckState checkState = new CheckState();
                    BuyState buyState = new BuyState();
                    var l = List.of(selectState, interactState, checkState, buyState);
                    l.forEach(State::initState);

                    TradeWorkflow.STATES.addAll(l);

                    ClientPlayerEntity player = context.getSource().getPlayer();
                    player.sendMessage(Text.of("SELECTING, INTERACTING, CHECKING and BUYING"));
                    return 0;
                }))
                .then(literal("place").executes(context -> {
                    var l = List.of(new PlaceState());
                    l.forEach(State::initState);

                    TradeWorkflow.STATES.addAll(l);
                    ClientPlayerEntity player = context.getSource().getPlayer();
                    player.sendMessage(Text.of("PLACE"));
                    return 0;
                }))
                .then(literal("loadscript").executes(context -> {
                    String skript = """
                            # Hihi
                            
                            LOOK ~
                            WALK 3 3
                            LOOK -4.1 28.2
                            SELECT
                            """;
                    List<State> states = tradeWorkflow.getHandler(ScriptManager.class)
                            .parseScript(skript.lines().toList());
                    states.forEach(State::initState);

                    TradeWorkflow.STATES.addAll(states);

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
