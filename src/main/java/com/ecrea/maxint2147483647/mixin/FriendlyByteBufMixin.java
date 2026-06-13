package com.ecrea.maxint2147483647.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

/**
 * ネットワーク通信時のアイテム個数のByte制限(-128~127)を突破する。
 * バニラの writeItem / readItem は個数を writeByte / readByte しているため、
 * これを writeVarInt / readVarInt に書き換える。
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {

    @org.spongepowered.asm.mixin.injection.Inject(method = "writeItem", at = @org.spongepowered.asm.mixin.injection.At("HEAD"), cancellable = true)
    public void onWriteItem(ItemStack p_130055_, org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<FriendlyByteBuf> cir) {
        FriendlyByteBuf buf = (FriendlyByteBuf) (Object) this;
        if (p_130055_.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            Item item = p_130055_.getItem();
            buf.writeId(BuiltInRegistries.ITEM, item);
            
            buf.writeVarInt(p_130055_.getCount());
            
            CompoundTag compoundtag = null;
            if (item.canBeDepleted() || item.shouldOverrideMultiplayerNbt()) {
                compoundtag = p_130055_.getTag();
            }

            buf.writeNbt(compoundtag);
        }
        cir.setReturnValue(buf);
    }

    @org.spongepowered.asm.mixin.injection.Inject(method = "readItem", at = @org.spongepowered.asm.mixin.injection.At("HEAD"), cancellable = true)
    public void onReadItem(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<ItemStack> cir) {
        FriendlyByteBuf buf = (FriendlyByteBuf) (Object) this;
        if (!buf.readBoolean()) {
            cir.setReturnValue(ItemStack.EMPTY);
        } else {
            Item item = buf.readById(BuiltInRegistries.ITEM);
            
            int count = buf.readVarInt();
            
            ItemStack itemstack = new ItemStack(item, count);
            itemstack.setTag(buf.readNbt());
            cir.setReturnValue(itemstack);
        }
    }
}
