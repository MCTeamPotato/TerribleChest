package com.teampotato.terriblechest.init;


import com.teampotato.terriblechest.network.Networks;
import com.teampotato.terriblechest.network.message.gui.ChangePage;
import com.teampotato.terriblechest.network.message.gui.UnlockMaxPage;

import java.util.concurrent.atomic.AtomicInteger;

public class NetworkInitializer {
    public static void registerMessage() {
        AtomicInteger id = new AtomicInteger(0);
        Networks.getChannel().registerMessage(id.getAndIncrement(), ChangePage.class, ChangePage::encode, ChangePage::decode, ChangePage::handle);
        Networks.getChannel().registerMessage(id.getAndIncrement(), UnlockMaxPage.class, UnlockMaxPage::encode, UnlockMaxPage::decode, UnlockMaxPage::handle);
    }
}