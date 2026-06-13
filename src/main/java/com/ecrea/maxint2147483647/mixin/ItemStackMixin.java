package com.ecrea.maxint2147483647.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(method = "enchant", at = @At("RETURN"), require = 0)
    private void onEnchant(Enchantment enchantment, int level, CallbackInfo ci) {
        ItemStack self = (ItemStack) (Object) this;
        ListTag list = self.getEnchantmentTags();
        if (!list.isEmpty()) {
            CompoundTag last = list.getCompound(list.size() - 1);
            last.putInt("lvlInt", level);
        }
    }

    @Inject(method = "getMaxStackSize()I", at = @At("RETURN"), cancellable = true)
    private void onGetMaxStackSize(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(com.ecrea.maxint2147483647.config.MaxIntConfig.itemMaxStackSize());
    }
    @org.spongepowered.asm.mixin.Shadow
    public abstract int getCount();

    @org.spongepowered.asm.mixin.Shadow
    public abstract void setCount(int count);

    @Inject(method = "save", at = @At("RETURN"))
    private void onSave(CompoundTag tag, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<CompoundTag> cir) {
        // バニラの save 処理の後に、真の個数を ExtendedCount (Int) として追加保存する。
        // また、互換性のためバニラの Count (Byte) には 127 にクランプした値を入れておく。
        int currentCount = this.getCount();
        tag.putInt("ExtendedCount", currentCount);
        tag.putByte("Count", (byte) Math.min(127, currentCount));
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At("RETURN"))
    private void onInitFromTag(CompoundTag tag, CallbackInfo ci) {
        // NBTからのロード時、ExtendedCount が存在すればその値で個数を上書きする。
        if (tag.contains("ExtendedCount", 3 /* TAG_INT */)) {
            this.setCount(tag.getInt("ExtendedCount"));
        }
    }
}
