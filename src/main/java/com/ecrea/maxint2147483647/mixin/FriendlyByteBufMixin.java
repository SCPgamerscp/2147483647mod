package com.ecrea.maxint2147483647.mixin;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

/**
 * ネットワーク通信時のアイテム個数のByte制限(-128~127)を突破する。
 * バニラの writeItem / readItem は個数を writeByte / readByte しているため、
 * これを writeVarInt / readVarInt に書き換える。
 * ※ これにより、このMODを導入していないクライアント・サーバー間では接続できなくなります。
 */
@Mixin(FriendlyByteBuf.class)
public abstract class FriendlyByteBufMixin {

    @Shadow public abstract FriendlyByteBuf writeBoolean(boolean p_130043_);
    @Shadow public abstract <T> FriendlyByteBuf writeId(net.minecraft.core.IdMap<T> p_236815_, T p_236816_);
    @Shadow public abstract FriendlyByteBuf writeVarInt(int p_130131_);
    @Shadow public abstract FriendlyByteBuf writeNbt(CompoundTag p_130061_);

    @Shadow public abstract boolean readBoolean();
    @Shadow public abstract <T> T readById(net.minecraft.core.IdMap<T> p_236854_);
    @Shadow public abstract int readVarInt();
    @Shadow public abstract CompoundTag readNbt();

    /**
     * @author Antigravity
     * @reason アイテム個数を Byte ではなく VarInt として送る
     */
    @Overwrite
    public FriendlyByteBuf writeItem(ItemStack p_130055_) {
        if (p_130055_.isEmpty()) {
            this.writeBoolean(false);
        } else {
            this.writeBoolean(true);
            Item item = p_130055_.getItem();
            this.writeId(BuiltInRegistries.ITEM, item);
            
            // バニラ: this.writeByte(p_130055_.getCount());
            // 変更後:
            this.writeVarInt(p_130055_.getCount());
            
            CompoundTag compoundtag = null;
            if (item.canBeDepleted() || item.shouldOverrideMultiplayerNbt()) {
                compoundtag = p_130055_.getTag();
            }

            this.writeNbt(compoundtag);
        }
        return (FriendlyByteBuf) (Object) this;
    }

    /**
     * @author Antigravity
     * @reason アイテム個数を Byte ではなく VarInt として読み取る
     */
    @Overwrite
    public ItemStack readItem() {
        if (!this.readBoolean()) {
            return ItemStack.EMPTY;
        } else {
            Item item = this.readById(BuiltInRegistries.ITEM);
            
            // バニラ: int i = this.readByte();
            // 変更後:
            int count = this.readVarInt();
            
            ItemStack itemstack = new ItemStack(item, count);
            itemstack.setTag(this.readNbt());
            return itemstack;
        }
    }
}
