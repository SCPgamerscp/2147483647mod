package com.ecrea.maxint2147483647.item;

import com.ecrea.maxint2147483647.MaxIntMod;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, MaxIntMod.MODID);

    // 空のポーション。他Modが「バニラの水瓶」だと誤認してカスタムNBTを消すのを防ぐための専用ベース
    public static final RegistryObject<Potion> AMPLIFIED = POTIONS.register("amplified", () -> new Potion("amplified"));
}
