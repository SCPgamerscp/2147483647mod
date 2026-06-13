package com.ecrea.maxint2147483647.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 2147483647mod の設定値。
 * itemMaxStackSize / enchantMaxLevel / effectMaxAmplifier はデフォルトで
 * Integer.MAX_VALUE (2147483647)。
 * stackCountDisplayMode はクライアント側のスタック数表示形式。
 */
public class MaxIntConfig {

    public enum CountDisplayMode {
        FULL_NUMBER,
        ABBREVIATED
    }

    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.IntValue ITEM_MAX_STACK_SIZE;
    public static final ForgeConfigSpec.IntValue ENCHANT_MAX_LEVEL;
    public static final ForgeConfigSpec.IntValue EFFECT_MAX_AMPLIFIER;

    public static final ForgeConfigSpec.EnumValue<CountDisplayMode> COUNT_DISPLAY_MODE;

    static {
        ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();
        common.push("maxint2147483647");

        ITEM_MAX_STACK_SIZE = common
                .comment("全アイテムの最大スタック数 (デフォルト: 2147483647)")
                .defineInRange("itemMaxStackSize", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

        ENCHANT_MAX_LEVEL = common
                .comment("コマンド/金床で付与できるエンチャントの最大レベル (デフォルト: 2147483647)")
                .defineInRange("enchantMaxLevel", Integer.MAX_VALUE, 1, Integer.MAX_VALUE);

        EFFECT_MAX_AMPLIFIER = common
                .comment("effectコマンド/醸造で付与できるポーション効果の最大増幅値 (デフォルト: 2147483647)")
                .defineInRange("effectMaxAmplifier", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

        common.pop();
        COMMON_SPEC = common.build();

        ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
        client.push("maxint2147483647");

        COUNT_DISPLAY_MODE = client
                .comment("アイテムスタック数の表示形式: FULL_NUMBER=そのままの数字, ABBREVIATED=省略表記(1.2Bなど)")
                .defineEnum("stackCountDisplayMode", CountDisplayMode.FULL_NUMBER);

        client.pop();
        CLIENT_SPEC = client.build();
    }

    public static int itemMaxStackSize() {
        return ITEM_MAX_STACK_SIZE.get();
    }

    public static int enchantMaxLevel() {
        return ENCHANT_MAX_LEVEL.get();
    }

    public static int effectMaxAmplifier() {
        return EFFECT_MAX_AMPLIFIER.get();
    }

    public static CountDisplayMode countDisplayMode() {
        return COUNT_DISPLAY_MODE.get();
    }
}
