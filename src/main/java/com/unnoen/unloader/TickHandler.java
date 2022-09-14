package com.unnoen.unloader;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
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
        WorldServer ws = DimensionManager.getWorld(id);
        WorldProvider wp = ws.provider;

        String dimName = wp.getDimensionName();

        // Do not continue if dimension name or id blacklisted
        for (String re : UnloaderConfig.blacklistDims) {
            if (dimName.matches(re)) {
                return;
            }
            if (Integer.toString(id).matches(re)) {
                return;
            }
        }

        IChunkProvider cp = ws.getChunkProvider();
        // This logic is correct
        // Do not continue if any loaded chunks
        if (cp.getLoadedChunkCount() != 0) {
            return;
        }
        // This logic is backwards in yamp fork?
        // Do not continue if any persistentChunks
        if (!ForgeChunkManager.getPersistentChunksFor(ws).isEmpty()) {
            return;
        }
        // This logic is backwards in yamp fork?
        // Do not continue if any players in dim
        if (!ws.playerEntities.isEmpty()) {
            return;
        }
        // This logic is backwards in yamp fork?
        // Do not continue if any entities are loaded
        if (!ws.loadedEntityList.isEmpty()) {
            return;
        }
        // This logic is backwards in yamp fork?
        // Do not continue if any tile ents are loaded
        if (!ws.loadedTileEntityList.isEmpty()) {
            return;
        }

        try {
            ws.saveAllChunks(true, null);
        } catch (MinecraftException e) {
            logger.error("Caught an exception while saving all chunks:", e);
        } finally {
            MinecraftForge.EVENT_BUS.post(new WorldEvent.Unload(ws));
            ws.flush();
            DimensionManager.setWorld(id, null);
        }
    }
}
