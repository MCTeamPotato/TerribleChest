package com.teampotato.terriblechest.settings;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class Config {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final Config.Common COMMON;

    static {
        Pair<Common, ForgeConfigSpec> common = (new ForgeConfigSpec.Builder()).configure(Config.Common::new);
        COMMON_SPEC = common.getRight();
        COMMON = common.getLeft();
    }

    public static final class Common {
        public final ForgeConfigSpec.BooleanValue useSinglePageMode;
        public final ForgeConfigSpec.LongValue slotStackLimit;
        public final ForgeConfigSpec.IntValue maxPageLimit;
        public final ForgeConfigSpec.IntValue resetMaxPage;
        public final ForgeConfigSpec.IntValue inventoryRows;

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("").push("common");
            this.useSinglePageMode = builder.comment("Use single page mode").translation("config.terrible_chest.use_single_page_mode").define("useSinglePageMode", false);
            this.slotStackLimit = builder.comment("Stack size limit of slot").translation("config.terrible_chest.slot_stack_limit").defineInRange("slotStackLimit", 4294967295L, 64L, 4294967295L);
            this.maxPageLimit = builder.comment("Maximum page limit").translation("config.terrible_chest.max_page_limit").defineInRange("maxPageLimit", 79536431, 2, 79536431);
            this.resetMaxPage = builder.comment("*Recovery options* Reset the max page that is 0.").translation("config.terrible_chest.reset_max_page").defineInRange("resetMaxPage", 1, 1, 79536431);
            this.inventoryRows = builder.comment("Inventory rows for multi page mode").translation("config.terrible_chest.inventory_rows").defineInRange("inventoryRows", 3, 1, 6);
            builder.pop();
        }
    }
}

