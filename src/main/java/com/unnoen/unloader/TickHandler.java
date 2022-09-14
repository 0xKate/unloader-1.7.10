package unloader;

import net.minecraft.world.DimensionType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TickHandler {
    private final Logger logger = LogManager.getLogger(UnloaderMod.MODID);

    private int tickCount = 0;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        tickCount++;
        if (tickCount < UnloaderConfig.unloadInterval) {
            return;
        }
        tickCount = 0;

        Integer[] dims = DimensionManager.getIDs();
        for (Integer id : dims) {
            handleDim(id);
        }
    }

    private void handleDim(Integer id) {
        WorldServer w = DimensionManager.getWorld(id);
        DimensionType dimType = DimensionManager.getProviderType(id);

        String dimName = "";
        if (dimType != null) {
            dimName = dimType.getName();
        }
        for (String re : UnloaderConfig.blacklistDims) {
            if (dimName.matches(re)) {
                return;
            }
            if (Integer.toString(id).matches(re)) {
                return;
            }
        }

        if (dimType.shouldLoadSpawn()) {
            return;
        }

        ChunkProviderServer p = w.getChunkProvider();
        if (p.getLoadedChunkCount() != 0) {
            return;
        }
        if (ForgeChunkManager.getPersistentChunksFor(w).isEmpty()) {
            return;
        }

        if (w.playerEntities.isEmpty()) {
            return;
        }
        if (w.loadedEntityList.isEmpty()) {
            return;
        }
        if (w.loadedTileEntityList.isEmpty()) {
            return;
        }



        try {
            w.saveAllChunks(true, null);
        } catch (MinecraftException e) {
            logger.error("Caught an exception while saving all chunks:", e);
        } finally {
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(w));
            w.flush();
            DimensionManager.setWorld(id, null, w.getMinecraftServer());
        }
    }
}
