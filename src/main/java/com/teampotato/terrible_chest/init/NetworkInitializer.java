package com.teampotato.terrible_chest.init;


import com.teampotato.terrible_chest.network.Networks;
import com.teampotato.terrible_chest.network.message.gui.ChangePage;
import com.teampotato.terrible_chest.network.message.gui.UnlockMaxPage;

import java.util.concurrent.atomic.AtomicInteger;

public class NetworkInitializer {
    public static void registerMessage() {
        AtomicInteger id = new AtomicInteger(0);
        Networks.getChannel().registerMessage(id.getAndIncrement(), ChangePage.class, ChangePage::encode, ChangePage::decode, ChangePage::handle);
        Networks.getChannel().registerMessage(id.getAndIncrement(), UnlockMaxPage.class, UnlockMaxPage::encode, UnlockMaxPage::decode, UnlockMaxPage::handle);
    }
}