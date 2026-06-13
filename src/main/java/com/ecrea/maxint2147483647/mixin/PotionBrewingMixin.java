package com.ecrea.maxint2147483647.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {

    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private static void onMix(ItemStack potion, ItemStack ingredient, CallbackInfoReturnable<ItemStack> cir) {
        if (potion.isEmpty() || ingredient.isEmpty()) return;
        if (!(potion.getItem() instanceof PotionItem)) return;

        Item ingredientItem = ingredient.getItem();
        boolean isGlowstone = ingredientItem == Items.GLOWSTONE_DUST;
        boolean isRedstone = ingredientItem == Items.REDSTONE;

        // グロウストーンかレッドストーンの場合のみ、無限強化ロジックを発動
        if (isGlowstone || isRedstone) {
            List<MobEffectInstance> effects = PotionUtils.getMobEffects(potion);
            if (effects.isEmpty()) return; // 水瓶などはスキップし、バニラのロジック（弱化のポーション等）に任せる

            // 新しいポーションアイテムを作成
            ItemStack output = new ItemStack(potion.getItem());
            
            // 他Modからのバニラレシピ誤認を防ぐため、安全な "minecraft:water" などのベースにするのではなく、
            // 空の NBT を作成し、CustomPotionEffects のみを保存する
            output.getOrCreateTag().putString("Potion", "minecraft:water"); // これがないと一部処理で壊れるため水ベースにする

            List<MobEffectInstance> newEffects = new ArrayList<>();
            int maxAmp = 0;
            
            for (MobEffectInstance effect : effects) {
                int newAmp = effect.getAmplifier();
                int newDuration = effect.getDuration();
                
                if (isGlowstone) {
                    if (newAmp < Integer.MAX_VALUE) {
                        newAmp += 1;
                    }
                }
                if (isRedstone) {
                    // レッドストーンの場合は時間を1.5倍にする（最小+1200tick）
                    if (newDuration < Integer.MAX_VALUE / 2) {
                        newDuration = Math.max(newDuration + 1200, (int)(newDuration * 1.5));
                    }
                }
                
                maxAmp = Math.max(maxAmp, newAmp);
                newEffects.add(new MobEffectInstance(effect.getEffect(), newDuration, newAmp, effect.isAmbient(), effect.isVisible(), effect.showIcon()));
            }

            // 元のポーションのカスタム色があれば引き継ぎ、なければ計算して付与
            int color = PotionUtils.getColor(potion);
            if (color != 3694022) { // 水の色でなければ
                output.getOrCreateTag().putInt("CustomPotionColor", color);
            } else {
                output.getOrCreateTag().putInt("CustomPotionColor", PotionUtils.getColor(newEffects));
            }

            PotionUtils.setCustomEffects(output, newEffects);

            // ポーションの名前にレベルを付与する（例: "+5" など）
            // バニラポーションの翻訳名を引き継ぐのは難しいため、HoverNameを設定
            Component oldName = potion.getHoverName();
            String nameStr = oldName.getString();
            // 名前から古い "+X" を除去して付け直す簡易ロジック
            if (nameStr.contains(" +")) {
                nameStr = nameStr.substring(0, nameStr.lastIndexOf(" +"));
            }
            if (maxAmp > 0) {
                output.setHoverName(Component.literal(nameStr + " +" + maxAmp));
            } else {
                output.setHoverName(Component.literal(nameStr + " (Extended)"));
            }

            cir.setReturnValue(output);
        }
    }
}
