package com.ecrea.maxint2147483647.brewing;

import com.ecrea.maxint2147483647.config.MaxIntConfig;
import com.ecrea.maxint2147483647.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraftforge.common.brewing.IBrewingRecipe;

import java.util.ArrayList;
import java.util.List;

/**
 * 醸造台で「ポーション + 増幅結晶」を組み合わせると、
 * ポーションに含まれる全ての効果のamplifier(増幅値)を+1して
 * 再醸造できるレシピ。何度も繰り返すことで増幅値を無制限に
 * 増やすことができる。
 *
 * 出力はバニラのPotionタグを持たない純粋なカスタムポーションとなり、
 * 元のポーション効果との競合(再生→暗視に変わるなど)を防ぐ。
 * ポーション名は効果に基づいて自動的に生成される。
 */
public class AmplifyingBrewingRecipe implements IBrewingRecipe {

    @Override
    public boolean isInput(ItemStack input) {
        if (input.getItem() != Items.POTION) {
            return false;
        }
        return !PotionUtils.getMobEffects(input).isEmpty();
    }

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        return ingredient.getItem() == ModItems.AMPLIFYING_CRYSTAL.get();
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!isInput(input) || !isIngredient(ingredient)) {
            return ItemStack.EMPTY;
        }

        int maxAmplifier = MaxIntConfig.effectMaxAmplifier();
        List<MobEffectInstance> source = PotionUtils.getMobEffects(input);
        List<MobEffectInstance> result = new ArrayList<>(source.size());

        for (MobEffectInstance effect : source) {
            int newAmplifier = effect.getAmplifier() + 1;
            if (newAmplifier > maxAmplifier) {
                newAmplifier = maxAmplifier;
            }
            int newDuration = effect.getDuration();
            if (newDuration < Integer.MAX_VALUE / 2) {
                newDuration = Math.max(newDuration + 1200, (int)(newDuration * 1.5));
            }
            result.add(new MobEffectInstance(
                    effect.getEffect(),
                    newDuration,
                    newAmplifier,
                    effect.isAmbient(),
                    effect.isVisible(),
                    effect.showIcon()
            ));
        }

        ItemStack output = new ItemStack(Items.POTION);

        // ─── バニラの Potion タグを専用のベースポーションに設定 ───
        // minecraft:water だと他Modに「バニラの水瓶が作られた」と誤認されてNBTが消されるため、
        // 独自のポーション(maxint2147483647:amplified)に偽装して他Modからの干渉を防ぐ。
        output.getOrCreateTag().putString("Potion", "maxint2147483647:amplified");

        // ─── カスタムエフェクトのみ設定 ───
        PotionUtils.setCustomEffects(output, result);

        // ─── ポーション色を元のポーションに合わせて設定 ───
        output.getOrCreateTag().putInt("CustomPotionColor", PotionUtils.getColor(input));

        // ─── カスタム名を設定 (例: "再生 III", "力 V + 耐性 V") ───
        output.setHoverName(buildPotionName(result));

        return output;
    }

    /**
     * ポーション効果から表示名を生成する。
     * 単一効果: "再生 III"
     * 複数効果: "再生 III + 力 V"
     *
     * レベルは 1〜10 はローマ数字 (Minecraftの翻訳キー使用)、
     * 11以上はアラビア数字で表示する。
     */
    private Component buildPotionName(List<MobEffectInstance> effects) {
        if (effects.isEmpty()) {
            return Component.translatable("item.minecraft.potion");
        }

        MutableComponent name = Component.empty();
        for (int i = 0; i < effects.size(); i++) {
            if (i > 0) {
                name.append(" + ");
            }
            MobEffectInstance effect = effects.get(i);
            name.append(effect.getEffect().getDisplayName().copy());

            int level = effect.getAmplifier() + 1;
            if (level > 1) {
                name.append(" ");
                if (level <= 10) {
                    name.append(Component.translatable("enchantment.level." + level));
                } else {
                    name.append(Component.literal(String.valueOf(level)));
                }
            }
        }

        return name;
    }
}
