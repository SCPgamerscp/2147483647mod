package com.ecrea.maxint2147483647.item;

import com.ecrea.maxint2147483647.MaxIntMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MaxIntMod.MODID);

    public static final RegistryObject<CreativeModeTab> MAIN_TAB = TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.maxint2147483647"))
                    .icon(() -> new ItemStack(ModItems.AMPLIFYING_CRYSTAL.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.AMPLIFYING_CRYSTAL.get());
                    })
                    .build());
}
