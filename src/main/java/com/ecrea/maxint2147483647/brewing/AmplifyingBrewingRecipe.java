package com.ecrea.maxint2147483647.brewing;

import com.ecrea.maxint2147483647.config.MaxIntConfig;
import com.ecrea.maxint2147483647.item.ModItems;
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
            result.add(new MobEffectInstance(
                    effect.getEffect(),
                    effect.getDuration(),
                    newAmplifier,
                    effect.isAmbient(),
                    effect.isVisible(),
                    effect.showIcon()
            ));
        }

        ItemStack output = new ItemStack(Items.POTION);
        PotionUtils.setCustomEffects(output, result);
        return output;
    }
}
