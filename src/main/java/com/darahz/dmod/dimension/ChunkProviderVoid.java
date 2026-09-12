package com.darahz.dmod.dimension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

/** Generates ordinary, editable air chunks. The server chunk provider handles saving. */
public class ChunkProviderVoid implements IChunkProvider {
    private final World world;

    public ChunkProviderVoid(World world) {
        this.world = world;
    }

    @Override
    public Chunk provideChunk(int x, int z) {
        Chunk chunk = new Chunk(world, x, z);
        Arrays.fill(chunk.getBiomeArray(), (byte) BiomeGenBase.plains.biomeID);
        chunk.generateSkylightMap();
        return chunk;
    }

    @Override public Chunk loadChunk(int x, int z) { return provideChunk(x, z); }
    @Override public boolean chunkExists(int x, int z) { return true; }
    @Override public void populate(IChunkProvider provider, int x, int z) {}
    @Override public void recreateStructures(int x, int z) {}
    @Override public boolean saveChunks(boolean all, IProgressUpdate progress) { return true; }
    @Override public void saveExtraData() {}
    @Override public boolean canSave() { return true; }
    @Override public boolean unloadQueuedChunks() { return false; }
    @Override public int getLoadedChunkCount() { return 0; }
    @Override public String makeString() { return "dModVoid"; }

    @Override
    public List getPossibleCreatures(EnumCreatureType type, int x, int y, int z) {
        return Collections.emptyList();
    }

    @Override
    public ChunkPosition func_147416_a(World world, String structure, int x, int y, int z) {
        return null;
    }
}
