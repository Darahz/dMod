package com.darahz.dmod;

/** Constants shared across the mod. */
public final class Reference {

    public static final String MODID = "dmod";
    public static final String NAME = "dMod";
    /** Keep in step with the `version` in build.gradle. */
    public static final String VERSION = "1.0.0";

    public static final String CLIENT_PROXY = "com.darahz.dmod.proxy.ClientProxy";
    public static final String SERVER_PROXY = "com.darahz.dmod.proxy.CommonProxy";

    /** Prefix for resource locations, e.g. "dmod:necklaceofrepair". */
    public static final String RES = MODID + ":";

    private Reference() {}
}
