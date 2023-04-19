package com.teampotato.terriblechest.init;

import com.teampotato.terriblechest.gui.TerribleChestScreen;
import com.teampotato.terriblechest.inventory.container.ContainerTypes;
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
