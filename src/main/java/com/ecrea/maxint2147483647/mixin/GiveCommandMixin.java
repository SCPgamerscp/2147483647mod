package com.ecrea.maxint2147483647.mixin;

import net.minecraft.server.commands.GiveCommand;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(GiveCommand.class)
public class GiveCommandMixin {
    @Redirect(method = "giveItem",
              at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;getMaxStackSize()I"),
              require = 0)
    private static int fixOverflow(Item item) {
        int real = item.getMaxStackSize();
        return Math.min(real, Integer.MAX_VALUE / 100);
    }
}
