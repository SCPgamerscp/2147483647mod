package com.ecrea.maxint2147483647.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "setEnchantments", at = @At("RETURN"), require = 0)
    private static void addIntLevels(Map<Enchantment, Integer> enchantments, ItemStack stack, CallbackInfo ci) {
        CompoundTag stackTag = stack.getTag();
        if (stackTag == null || !stackTag.contains("Enchantments", 9)) return;

        ListTag list = stackTag.getList("Enchantments", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            String id = tag.getString("id");
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                if (entry.getKey() == null) continue;
                ResourceLocation enchId = BuiltInRegistries.ENCHANTMENT.getKey(entry.getKey());
                if (enchId != null && enchId.toString().equals(id)) {
                    tag.putInt("lvlInt", entry.getValue());
                    break;
                }
            }
        }
    }

    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/nbt/CompoundTag;)I", at = @At("HEAD"), cancellable = true, require = 0)
    private static void overrideEnchantmentLevelClamp(CompoundTag tag, CallbackInfoReturnable<Integer> cir) {
        if (tag.contains("lvlInt")) {
            cir.setReturnValue(tag.getInt("lvlInt"));
        }
    }
}
