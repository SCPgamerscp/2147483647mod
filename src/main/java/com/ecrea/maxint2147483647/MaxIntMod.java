package com.ecrea.maxint2147483647;

import com.ecrea.maxint2147483647.config.MaxIntConfig;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(MaxIntMod.MODID)
public class MaxIntMod {

    public static final String MODID = "maxint2147483647";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MaxIntMod() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        com.ecrea.maxint2147483647.item.ModPotions.POTIONS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MaxIntConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, MaxIntConfig.CLIENT_SPEC);

        modEventBus.addListener(this::commonSetup);

        LOGGER.info("2147483647mod loaded. Item stack size / enchant level / effect amplifier up to {}", Integer.MAX_VALUE);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // Setup logic here if needed
    }
}
