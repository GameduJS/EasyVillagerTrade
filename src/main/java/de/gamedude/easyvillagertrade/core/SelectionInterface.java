package de.gamedude.easyvillagertrade.core;

import de.gamedude.easyvillagertrade.utils.TradingState;
import net.minecraft.block.Block;
import net.minecraft.block.LecternBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;

import java.util.Optional;

public class SelectionInterface {

    private final EasyVillagerTradeBase easyVillagerTradeBase;
    private VillagerEntity villager;
    private BlockPos lecternPos;

    public SelectionInterface(EasyVillagerTradeBase easyVillagerTradeBase) {
        this.easyVillagerTradeBase = easyVillagerTradeBase;
    }

    public VillagerEntity getVillager() { return villager; }
    public void setVillager(VillagerEntity villager) { this.villager = villager; }

    public BlockPos getLecternPos() { return lecternPos; }
    public void setLecternPos(BlockPos blockPos) { this.lecternPos = blockPos; }


    public void selectClosestToPlayer(ClientPlayerEntity player) {
        Optional<BlockPos> closestBlockOptional = BlockPos.findClosest(player.getBlockPos(), 3, 0, blockPos -> player.world.getBlockState(blockPos).getBlock() instanceof LecternBlock);
        if(closestBlockOptional.isEmpty()) {
            player.sendMessage(Text.of("§8| §cUnable to find any lecterns close to you"));
            easyVillagerTradeBase.setState(TradingState.INACTIVE);
            return;
        }
        this.lecternPos = closestBlockOptional.get();

        this.villager = getClosestEntity(player.world, this.lecternPos);
        if(this.villager == null) {
            player.sendMessage(Text.of("§8| §cUnable to find a suitable villager close to the lectern"));
            easyVillagerTradeBase.setState(TradingState.INACTIVE);
        }
    }

    private VillagerEntity getClosestEntity(World world, BlockPos blockPos) {
        VillagerEntity entity = null;
        double dist = Double.MAX_VALUE;

        for(VillagerEntity villagerEntity : world.getEntitiesByClass(VillagerEntity.class, new Box(blockPos).expand(3), (villager) -> villager.getVillagerData().getProfession() == VillagerProfession.LIBRARIAN)) {
            double distanceSquared = villagerEntity.squaredDistanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ());

            if(distanceSquared < dist) {
                dist = distanceSquared;
                entity = villagerEntity;
            }
        }
        return entity;
    }
}
