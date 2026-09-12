package com.darahz.dmod.dimension;

import java.io.File;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;

public final class VoidDimension {
    public static int dimensionId;

    private VoidDimension() {}

    public static void register(File configFile) {
        Configuration config = new Configuration(configFile);
        config.load();
        dimensionId = config.getInt("voidDimensionId", "dimensions", 72, 2, 127,
                "Dimension and provider ID for dMod's void. Keep unchanged for existing worlds; use the same ID on server and client.");
        if (config.hasChanged()) {
            config.save();
        }
        if (DimensionManager.isDimensionRegistered(dimensionId)
                || !DimensionManager.registerProviderType(dimensionId, WorldProviderVoid.class, false)) {
            throw new IllegalStateException("dMod void dimension ID " + dimensionId
                    + " is already in use. Choose a free voidDimensionId in config/dmod.cfg.");
        }
        DimensionManager.registerDimension(dimensionId, dimensionId);
    }
}
