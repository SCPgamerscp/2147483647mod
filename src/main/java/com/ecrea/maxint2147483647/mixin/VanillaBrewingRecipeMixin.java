package com.ecrea.maxint2147483647.mixin;

import com.ecrea.maxint2147483647.item.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.brewing.VanillaBrewingRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = VanillaBrewingRecipe.class, remap = false)
public class VanillaBrewingRecipeMixin {

    @Inject(method = "getOutput(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    private void onGetOutput(ItemStack input, ItemStack ingredient, CallbackInfoReturnable<ItemStack> cir) {
        if (!ingredient.isEmpty() && ingredient.getItem() == ModItems.AMPLIFYING_CRYSTAL.get()) {
            cir.setReturnValue(ItemStack.EMPTY); // バニラレシピとしてはマッチさせず、後続のカスタムレシピに回す
        }
    }
}
