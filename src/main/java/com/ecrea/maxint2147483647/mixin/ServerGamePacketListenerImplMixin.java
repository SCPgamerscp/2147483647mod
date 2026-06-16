package com.ecrea.maxint2147483647.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplMixin {

    /**
     * クリエイティブインベントリからアイテムを取り出す際の
     * サーバー側の「最大64個まで」というチート防止チェックを解除する。
     */
    @ModifyConstant(method = "handleSetCreativeModeSlot", constant = @Constant(intValue = 64))
    private int modifyCreativeMaxStackSize(int original) {
        return Integer.MAX_VALUE;
    }
}
