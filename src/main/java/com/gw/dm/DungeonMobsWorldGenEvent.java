package com.gw.dm;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
//import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;

public class DungeonMobsWorldGenEvent
{

    private final Set<Long> chunksGenerated = new HashSet<>();

    @SubscribeEvent
    public void onPopulateChunk(PopulateChunkEvent.Populate event) {

        long chunkId = (long) event.chunkX + event.chunkZ << 32;

        if(chunksGenerated.contains(chunkId)) {
            return;
        }

        this.generateTraps(event.chunkProvider, event.world, event.rand, event.chunkX, event.chunkZ);

        chunksGenerated.add(chunkId);

    }
    private void generateTraps(IChunkProvider chunkProvider, World world, Random rand, int chunkX, int chunkZ) {
        if (!world.getChunkProvider().chunkExists(chunkX, chunkZ)) {
            return;
        }
        int posX = chunkX << 4;
        int posZ = chunkZ << 4;
        ChunkPosition strongholdPos = chunkProvider.func_147416_a(world, "Stronghold", posX, 64, posZ);
        int strongholdX = strongholdPos.chunkPosX;
        int strongholdZ = strongholdPos.chunkPosZ;
        int max = 0;
        if (world.difficultySetting != EnumDifficulty.PEACEFUL) {
            max = rand.nextInt(8) + rand.nextInt(3) + 2;
            if (world.difficultySetting == EnumDifficulty.NORMAL)
                max += rand.nextInt(3) + 3;
            if (world.difficultySetting == EnumDifficulty.HARD) {
                max += rand.nextInt(3) + 3;
                max += rand.nextInt(3) + 3;
            }
        }
        for (int i = 0; i < max; i++) {
            int barX = strongholdX + (rand.nextInt(129) - 64);
            int barZ = strongholdZ + (rand.nextInt(129) - 64);

            for (int barY = 50; barY >= 0; barY--) {
                if (world.getBlock(barX, barY, barZ) == Blocks.stonebrick && world.isAirBlock(barX, barY + 1, barZ) && world.isAirBlock(barX, barY + 2, barZ)) {
                    Block blockAbove = world.getBlock(barX, barY + 1, barZ);
                    if (blockAbove == Blocks.air || blockAbove.isReplaceable(world, barX, barY + 1, barZ)) {
                        world.setBlock(barX, barY + 1, barZ, DungeonMobs.bladeTrap, 0, 3);
                        System.out.println("Blade trap generated at X: " + barX + ", Y: " + (barY + 1) + ", Z: " + barZ);
                        break;
                    }
                }
            }
        }
    }
}
