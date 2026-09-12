package com.darahz.dmod.world;

import com.darahz.dmod.DMod;
import cpw.mods.fml.common.IWorldGenerator;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;

public class ZeniteOreGenerator implements IWorldGenerator {
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world,
            IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.dimensionId != 0) {
            return;
        }
        WorldGenMinable vein = new WorldGenMinable(DMod.zeniteOre, 6, Blocks.stone);
        // Six attempts per new chunk, with vein origins between Y=8 and Y=40.
        for (int attempt = 0; attempt < 6; attempt++) {
            vein.generate(world, random, chunkX * 16 + random.nextInt(16),
                    8 + random.nextInt(33), chunkZ * 16 + random.nextInt(16));
        }
    }
}
