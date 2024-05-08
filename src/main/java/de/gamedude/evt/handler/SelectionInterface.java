package de.gamedude.evt.handler;

import net.minecraft.block.LecternBlock;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;

import java.util.Optional;

public class SelectionInterface implements Handler {

    private VillagerEntity villager;
    private BlockPos lecternPos;

    public VillagerEntity getVillager() {
        return villager;
    }

    public void setVillager(VillagerEntity villager) {
        this.villager = villager;
    }

    public BlockPos getLecternPos() {
        return lecternPos;
    }

    public void setLecternPos(BlockPos blockPos) {
        this.lecternPos = blockPos;
    }

    public int selectClosestToPlayer(PlayerEntity player) {
        Optional<BlockPos> closestBlockOptional = BlockPos.findClosest(player.getBlockPos(), 3, 0, blockPos -> player.getWorld().getBlockState(blockPos).getBlock() instanceof LecternBlock);
        if(closestBlockOptional.isEmpty())
            return 1;
        this.lecternPos = closestBlockOptional.get();
        this.villager = getClosestEntity(player.getWorld(), this.lecternPos);

        return (villager == null) ? 2 : 0;
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
