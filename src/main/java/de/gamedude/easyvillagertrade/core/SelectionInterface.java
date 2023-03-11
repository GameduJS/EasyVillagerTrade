package de.gamedude.easyvillagertrade.core;

import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;

public class SelectionInterface {

    private VillagerEntity villager;
    private BlockPos lecternPos;

    public VillagerEntity getVillager() { return villager; }
    public void setVillager(VillagerEntity villager) { this.villager = villager; }

    public BlockPos getLecternPos() { return lecternPos; }
    public void setLecternPos(BlockPos blockPos) { this.lecternPos = blockPos; }
}
