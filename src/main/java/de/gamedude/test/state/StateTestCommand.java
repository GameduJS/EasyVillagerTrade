package de.gamedude.test.state;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.gamedude.evt.EasyVillagerTrade;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.logic.*;
import de.gamedude.evt.script.Script;
import de.gamedude.evt.script.ScriptManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class StateTestCommand implements ClientCommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {

        dispatcher.register(literal("teststate")
                .then(argument("state", StringArgumentType.greedyString())
                .executes(context -> {
                    String stateRaw = StringArgumentType.getString(context, "state");

                    State state =  switch ( stateRaw.toLowerCase() ) {
                        case "break" -> {
                            if ( !(context.getSource().getClient().crosshairTarget instanceof BlockHitResult blockHitResult))
                                yield null;
                            yield new BreakState(blockHitResult.getBlockPos());
                        }
                        case "look" -> new LookState(180, 0);
                        case "walk" -> new WalkState(3, 3);
                        case "place" -> {
                            if ( !(context.getSource().getClient().crosshairTarget instanceof BlockHitResult blockHitResult))
                                yield null;
                            yield new PlaceState(blockHitResult.getBlockPos().up(1));
                        }
                        default -> null;
                    };

                    if ( state == null )
                        return 1;
                    Map<Script.ScriptType, List<State>> rawScript = new HashMap<>();
                    rawScript.put(Script.ScriptType.INIT, List.of(state));
                    TradeWorkflow.INSTANCE.getHandler(ScriptManager.class)
                            .setScript( new Script(rawScript) );

                    TradeWorkflow.INSTANCE.getHandler(ScriptManager.class).getScript().tickScriptType(Script.ScriptType.INIT);
                    TradeWorkflow.INSTANCE.toggle(true);

                    context.getSource().getPlayer().sendMessage(Text.of("Executing test state: " + stateRaw));
                    return 0;
                })));

    }
}
