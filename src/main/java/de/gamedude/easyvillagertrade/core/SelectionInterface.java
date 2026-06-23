package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradingState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class SelectionInterface {

    private final EasyVillagerTradeBase modBase;

    public SelectionInterface(EasyVillagerTradeBase modBase) {
        this.modBase = modBase;
    }

    private Villager villager;
    private BlockPos lecternPos;

    public Villager getVillager() { return villager; }
    public void setVillager(Villager villager) { this.villager = villager; }

    public BlockPos getLecternPos() { return lecternPos; }
    public void setLecternPos(BlockPos blockPos) { this.lecternPos = blockPos; }

    public int selectClosestToPlayer(Player player) {

        Optional<BlockPos> closestBlockOptional = BlockPos.findClosestMatch(player.blockPosition(), 3, 0, blockPos -> player.level().getBlockState(blockPos).getBlock() instanceof LecternBlock);
        if(closestBlockOptional.isEmpty()) {
            modBase.setState(TradingState.INACTIVE);
            return 1;
        }
        this.lecternPos = closestBlockOptional.get();

        this.villager = getClosestEntity(player.level(), this.lecternPos);
        if(this.villager == null) {
            modBase.setState(TradingState.INACTIVE);
            return 2;
        }
        modBase.setState(TradingState.INACTIVE);
        return 0;
    }

    private Villager getClosestEntity(Level world, BlockPos blockPos) {
        Villager entity = null;
        double dist = Double.MAX_VALUE;

        for(Villager villagerEntity : world.getEntities(EntityTypeTest.forClass(Villager.class), AABB.ofSize(Vec3.atCenterOf(blockPos), 3, 3, 3), (villager) -> villager.getVillagerData().profession().is(VillagerProfession.LIBRARIAN))) {
            double distanceSquared = villagerEntity.distanceToSqr(Vec3.atCenterOf(blockPos));
            if(distanceSquared < dist) {
                dist = distanceSquared;
                entity = villagerEntity;
            }
        }
        return entity;
    }
}
