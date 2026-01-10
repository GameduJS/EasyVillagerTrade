package de.gamedude.evt.handler;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.LecternBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class SelectionInterface implements Handler, UseBlockCallback, UseEntityCallback {

    private VillagerEntity villager;
    private BlockPos lecternPos;

    public Supplier<VillagerEntity> getVillager() {
        return () -> villager;
    }

    public Supplier<BlockPos> getLecternPos() {
        return () -> lecternPos;
    }

    public void setVillager(VillagerEntity villager) {
        this.villager = villager;
    }

    public void setLecternPos(BlockPos blockPos) {
        this.lecternPos = blockPos;
    }

    public int selectClosestToPlayer(PlayerEntity player) {
        Optional<BlockPos> closestBlockOptional = BlockPos.findClosest(player.getBlockPos(), 1, 0, blockPos -> player.getWorld().getBlockState(blockPos).getBlock() instanceof LecternBlock);
        if(closestBlockOptional.isEmpty())
            return 1;
        this.lecternPos = closestBlockOptional.get();
        this.villager = getClosestVillager(player.getWorld(), this.lecternPos);
        return (villager == null) ? 2 : 0;
    }

    private VillagerEntity getClosestVillager(World world, BlockPos blockPos) {
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

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if(!TradeWorkflow.INSTANCE.enableSelection)
            return ActionResult.PASS;
        if(!world.isClient)
            return ActionResult.PASS;
        if(!(world.getBlockState(hitResult.getBlockPos()).getBlock() instanceof LecternBlock))
            return ActionResult.PASS;
        this.setLecternPos(hitResult.getBlockPos());
        tryDisableSelection();
        player.sendMessage(Text.of("§aSuccessfully selected lectern!"));
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if(!TradeWorkflow.INSTANCE.enableSelection)
            return ActionResult.PASS;
        if(hitResult == null)
            return ActionResult.PASS;
        if(!world.isClient)
            return ActionResult.PASS;
        if(!(entity instanceof VillagerEntity villagerEntity))
            return ActionResult.PASS;
        this.setVillager(villagerEntity);
        tryDisableSelection();
        player.sendMessage(Text.of("§aSuccessfully selected villager!"));
        return ActionResult.SUCCESS;
    }

    public void startSelection() {
        TradeWorkflow.INSTANCE.enableSelection = true;
        this.villager = null;
        this.lecternPos = null;
    }

    private void tryDisableSelection() {
        if(!TradeWorkflow.INSTANCE.enableSelection)
            return;
        if(getVillager().get() != null && getLecternPos().get() != null)
            TradeWorkflow.INSTANCE.enableSelection = false;
    }
}
