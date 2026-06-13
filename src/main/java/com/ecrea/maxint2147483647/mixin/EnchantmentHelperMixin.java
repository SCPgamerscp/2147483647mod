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

/**
 * EnchantmentHelper のNBT読み書きを拡張。
 *
 * バニラでは enchantment level を short (putShort("lvl", (short) level)) で
 * 保存するため、32767 を超えるレベルがオーバーフローして壊れる。
 *
 * このMixinは:
 * - 書き込み時: "lvlInt" として int 値を追加保存 (バニラの "lvl" はそのまま残す)
 * - 読み込み時: "lvlInt" が存在すればそちらを優先的に使用
 *
 * MobEffectInstanceNBTMixin と同様のパターンで後方互換性を維持する。
 */
@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    // =========================================================================
    // 書き込み側: setEnchantments 完了後に lvlInt を追加
    // =========================================================================

    /**
     * EnchantmentHelper.setEnchantments(Map, ItemStack) の完了後、
     * 書き込まれた各エンチャントの CompoundTag に "lvlInt" を追加する。
     *
     * バニラは putShort("lvl", (short) level) でレベルを保存するため、
     * 保存後のNBTからは既にshortに丸められた値しか取得できない。
     * そこで元のMap引数からint値を取得して "lvlInt" に追記する。
     */
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

    // =========================================================================
    // 読み込み側: lvlInt が存在すれば優先的に使用
    // =========================================================================

    /**
     * getEnchantments(ItemStack) の戻り値マップを修正。
     * "lvlInt" タグが存在するエンチャントについて、int 値で上書きする。
     */
    @Inject(method = "getEnchantments(Lnet/minecraft/world/item/ItemStack;)Ljava/util/Map;",
            at = @At("RETURN"), require = 0)
    private static void fixReadLevels(ItemStack stack, CallbackInfoReturnable<Map<Enchantment, Integer>> cir) {
        Map<Enchantment, Integer> map = cir.getReturnValue();
        if (map.isEmpty()) return;

        ListTag list = stack.getEnchantmentTags();
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            if (!tag.contains("lvlInt")) continue;

            int fullLevel = tag.getInt("lvlInt");
            ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
            if (id == null) continue;

            Enchantment ench = BuiltInRegistries.ENCHANTMENT.get(id);
            if (ench != null && map.containsKey(ench)) {
                map.put(ench, fullLevel);
            }
        }
    }

    /**
     * getItemEnchantmentLevel(Enchantment, ItemStack) の戻り値を修正。
     * 対象エンチャントの CompoundTag に "lvlInt" があればそちらの値を返す。
     */
    @Inject(method = "getItemEnchantmentLevel", at = @At("RETURN"), cancellable = true, require = 0)
    private static void fixItemLevel(Enchantment enchantment, ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        ListTag list = stack.getEnchantmentTags();
        if (list.isEmpty()) return;

        ResourceLocation targetId = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
        if (targetId == null) return;

        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            if (!tag.contains("lvlInt")) continue;

            ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
            if (targetId.equals(id)) {
                cir.setReturnValue(tag.getInt("lvlInt"));
                return;
            }
        }
    }
}
