package com.ecrea.maxint2147483647.mixin;

import com.ecrea.maxint2147483647.config.MaxIntConfig;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.server.commands.EffectCommands;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * /effect コマンドの amplifier(増幅値)引数の範囲(バニラでは 0~255)を
 * Configの値(デフォルト Integer.MAX_VALUE)まで拡張する。
 *
 * EffectCommands#register 内には "amplifier"(0~255) と
 * "seconds"(0~1000000) の2つの IntegerArgumentType.integer 呼び出しが
 * あるため、max==255 の呼び出しのみを置き換える。
 */
@Mixin(EffectCommands.class)
public class EffectCommandsMixin {

    @Redirect(method = "register", at = @At(value = "INVOKE", target = "Lcom/mojang/brigadier/arguments/IntegerArgumentType;integer(II)Lcom/mojang/brigadier/arguments/IntegerArgumentType;"))
    private static IntegerArgumentType redirectIntegerArgument(int min, int max) {
        if (max == 255) {
            return IntegerArgumentType.integer(min, MaxIntConfig.effectMaxAmplifier());
        }
        return IntegerArgumentType.integer(min, max);
    }
}
