package unloader;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

@Mod(
        modid = UnloaderMod.MODID,
        name = UnloaderMod.NAME,
        version = "@VERSION@",
        acceptableRemoteVersions = "*")
public class UnloaderMod {
    public static final String MODID = "unloader";
    public static final String NAME = "Unloader";

    private TickHandler handler = null;

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        handler = new TickHandler();
        MinecraftForge.EVENT_BUS.register(handler);
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        MinecraftForge.EVENT_BUS.unregister(handler);
        handler = null;
    }
}
