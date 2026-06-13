package com.ecrea.maxint2147483647.item;

import com.ecrea.maxint2147483647.MaxIntMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MaxIntMod.MODID);

    /**
     * 増幅結晶: 醸造台でポーションと一緒に使うと、ポーション効果の
     * amplifier (増幅値) を +1 して再醸造できるアイテム。
     */
    public static final RegistryObject<Item> AMPLIFYING_CRYSTAL =
            ITEMS.register("amplifying_crystal", () -> new Item(new Item.Properties()));
}
