package com.unnoen.unloader;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.*;

@Mod(modid = UnloaderMod.MODID, name = UnloaderMod.NAME, version = "@VERSION@", acceptableRemoteVersions = "*")
public class UnloaderMod {
    public static final String MODID = "unloader";
    public static final String NAME = "Unloader";

    private TickHandler handler = null;

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        handler = new TickHandler();
        FMLCommonHandler.instance().bus().register(handler);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppedEvent event) {
        FMLCommonHandler.instance().bus().unregister(handler);
        handler = null;
    }
}
