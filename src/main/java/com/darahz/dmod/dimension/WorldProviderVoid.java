package com.darahz.dmod.dimension;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class WorldProviderVoid extends WorldProvider {
    @Override
    protected void registerWorldChunkManager() {
        worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.plains, 0.0F);
        // Skylight remains enabled for daylight lighting, despite the black sky.
        hasNoSky = false;
    }

    @Override
    public IChunkProvider createChunkGenerator() {
        return new ChunkProviderVoid(worldObj);
    }

    @Override public String getDimensionName() { return "dMod Void"; }
    // Vanilla skips sun, moon, stars and clouds for non-surface dimensions.
    @Override public boolean isSurfaceWorld() { return false; }
    @Override public boolean canRespawnHere() { return false; }
    @Override public boolean canCoordinateBeSpawn(int x, int z) { return true; }
    @Override public ChunkCoordinates getSpawnPoint() { return new ChunkCoordinates(0, 65, 0); }
    @Override public ChunkCoordinates getEntrancePortalLocation() { return getSpawnPoint(); }
    @Override public boolean isDaytime() { return true; }
    @Override public float calculateCelestialAngle(long time, float partialTicks) { return 0.0F; }
    @Override public long getWorldTime() { return 6000L; }
    @Override public int getMoonPhase(long time) { return 0; }

    @Override
    public void setWorldTime(long time) {
        // Do not mutate shared WorldInfo: the Overworld's clock must keep ticking.
        // Total world time still advances normally for scheduled block updates.
    }

    @Override public void calculateInitialWeather() { updateWeather(); }
    @Override public void resetRainAndThunder() { updateWeather(); }

    @Override
    public void updateWeather() {
        // Server strengths start at zero and never advance because we skip vanilla
        // weather updates. The strength setters exist only on the client in 1.7.10.
        if (worldObj.isRemote) {
            clearClientWeather();
        }
    }

    @SideOnly(Side.CLIENT)
    private void clearClientWeather() {
        worldObj.setRainStrength(0.0F);
        worldObj.setThunderStrength(0.0F);
    }

    @Override public boolean canDoLightning(Chunk chunk) { return false; }
    @Override public boolean canDoRainSnowIce(Chunk chunk) { return false; }
    @Override public boolean canSnowAt(int x, int y, int z, boolean checkLight) { return false; }
    @Override public boolean canBlockFreeze(int x, int y, int z, boolean byWater) { return false; }

    @Override @SideOnly(Side.CLIENT)
    public Vec3 getSkyColor(Entity camera, float partialTicks) {
        return Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
    }

    @Override @SideOnly(Side.CLIENT)
    public Vec3 getFogColor(float angle, float partialTicks) {
        return Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
    }

    @Override @SideOnly(Side.CLIENT)
    public boolean getWorldHasVoidParticles() { return false; }
}
