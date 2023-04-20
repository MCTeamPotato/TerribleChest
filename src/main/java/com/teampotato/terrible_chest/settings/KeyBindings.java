package com.teampotato.terrible_chest.settings;

import com.google.common.collect.Lists;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.settings.KeyConflictContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class KeyBindings {
    private static final List<KeyBinding> keyBindings;
    public static final KeyBinding SORT_0;
    public static final KeyBinding SORT_1;
    public static final KeyBinding SORT_2;
    public static final KeyBinding SORT_3;
    public static final KeyBinding SORT_4;
    public static final KeyBinding SORT_5;
    public static final KeyBinding SORT_6;
    public static final KeyBinding SORT_7;
    public static final KeyBinding SORT_8;
    public static final KeyBinding SORT_9;

    public static List<KeyBinding> getAll() {
        return keyBindings;
    }

    static {
        ArrayList<KeyBinding> list = Lists.newArrayList();
        SORT_0 = new KeyBinding("key.terrible_chest.sort0", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.0"), "key.terrible_chest");
        SORT_1 = new KeyBinding("key.terrible_chest.sort1", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.1"), "key.terrible_chest");
        SORT_2 = new KeyBinding("key.terrible_chest.sort2", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.2"), "key.terrible_chest");
        SORT_3 = new KeyBinding("key.terrible_chest.sort3", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.3"), "key.terrible_chest");
        SORT_4 = new KeyBinding("key.terrible_chest.sort4", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.4"), "key.terrible_chest");
        SORT_5 = new KeyBinding("key.terrible_chest.sort5", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.5"), "key.terrible_chest");
        SORT_6 = new KeyBinding("key.terrible_chest.sort6", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.6"), "key.terrible_chest");
        SORT_7 = new KeyBinding("key.terrible_chest.sort7", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.7"), "key.terrible_chest");
        SORT_8 = new KeyBinding("key.terrible_chest.sort8", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.8"), "key.terrible_chest");
        SORT_9 = new KeyBinding("key.terrible_chest.sort9", KeyConflictContext.GUI, InputMappings.getKey("key.keyboard.9"), "key.terrible_chest");
        Collections.addAll(list, SORT_0, SORT_1, SORT_2, SORT_3, SORT_4, SORT_5, SORT_6, SORT_7, SORT_8, SORT_9);
        keyBindings = Collections.unmodifiableList(list);
    }
}

