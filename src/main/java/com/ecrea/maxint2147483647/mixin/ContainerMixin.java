package com.ecrea.maxint2147483647.mixin;

import com.ecrea.maxint2147483647.config.MaxIntConfig;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Container.class)
public interface ContainerMixin {

    /**
     * @author Antigravity
     * @reason コンテナ内のデフォルトスタック上限(64)を突破するため
     */
    @Overwrite
    default int getMaxStackSize() {
        return MaxIntConfig.itemMaxStackSize();
    }
}
