package de.gamedude.easyvillagertrade.core.autowalk;

import de.gamedude.easyvillagertrade.config.Config;
import de.gamedude.easyvillagertrade.core.ConfigDependent;
import de.gamedude.easyvillagertrade.core.SelectionInterface;
import de.gamedude.easyvillagertrade.core.TradeWorkflowHandler;
import de.gamedude.easyvillagertrade.utils.ActionInterface;
import de.gamedude.easyvillagertrade.utils.TradingState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class VillagerHubEngine extends AutoWalkEngine implements ConfigDependent {

    private final TradeWorkflowHandler tradeWorkflowHandler;

    private Vec3d startPosition;
    private Vec3d offset;

    private int count = 1;

    public VillagerHubEngine(TradeWorkflowHandler tradeWorkflowHandler) {
        this.tradeWorkflowHandler = tradeWorkflowHandler;
    }

    public void onWalk(ClientPlayerEntity player) {
        Vec3d vec3d = startPosition.add(offset.multiply(count++));
        WalkAction walkAction = new WalkAction(vec3d, player);

        ((ActionInterface) player).easyVillagerTrade$setWalkAction(walkAction);
    }

    public void setDistance(Vec2f offset) {
        this.count = 1;
        this.offset = new Vec3d(offset.x, startPosition.getY(), offset.y);
    }

    public void setStartPosition(Vec3d startPosition) {
        this.startPosition = startPosition;
    }

    @Override
    protected void onFinish(PlayerEntity player) {
        ((ActionInterface) player).easyVillagerTrade$setWalkAction(null);
        getHandler(SelectionInterface.class).selectClosestToPlayer(player);
        this.tradeWorkflowHandler.setState(TradingState.CHECK_OFFERS);
        this.tradeWorkflowHandler.handleInteractionWithVillager();
    }

    @Override
    public void loadConfig(Config config) {

    }

    @Override
    public void reloadConfig(Config config) {

    }
}
