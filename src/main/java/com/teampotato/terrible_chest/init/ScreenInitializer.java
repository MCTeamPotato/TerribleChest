package com.teampotato.terrible_chest.init;

import com.teampotato.terrible_chest.gui.TerribleChestScreen;
import com.teampotato.terrible_chest.inventory.container.ContainerTypes;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenInitializer {
    public static void registerScreen() {
        ScreenManager.register(ContainerTypes.TERRIBLE_CHEST, TerribleChestScreen::createScreen);
        ScreenManager.register(ContainerTypes.TERRIBLE_CHEST_2, TerribleChestScreen::createScreen);
    }
}
