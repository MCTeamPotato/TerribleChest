package com.teampotato.terrible_chest.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.teampotato.terrible_chest.inventory.container.TerribleChestContainer;
import com.teampotato.terrible_chest.network.Networks;
import com.teampotato.terrible_chest.network.message.gui.ChangePage;
import com.teampotato.terrible_chest.network.message.gui.UnlockMaxPage;
import com.teampotato.terrible_chest.settings.Config;
import com.teampotato.terrible_chest.settings.KeyBindings;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@OnlyIn(Dist.CLIENT)
public abstract class TerribleChestScreen extends ContainerScreen<TerribleChestContainer> {
    private static final ResourceLocation MULTI_PAGE_TEXTURE = new ResourceLocation("terrible_chest", "textures/gui/container/terrible_chest_multi_page.png");
    private static final ResourceLocation SINGLE_PAGE_TEXTURE = new ResourceLocation("terrible_chest", "textures/gui/container/terrible_chest_single_page.png");
    private static final KeyBinding[] SORT_KEYS;

    private TerribleChestScreen(TerribleChestContainer screenContainer, PlayerInventory inv, ITextComponent titleIn, Object o) {
        super(screenContainer, inv, titleIn);
    }

    public static TerribleChestScreen createScreen(TerribleChestContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        return Config.COMMON.useSinglePageMode.get() ? new SinglePage(screenContainer, inv, titleIn) : new MultiPage(screenContainer, inv, titleIn);
    }

    protected void slotClicked(@Nullable Slot slotIn, int slotId, int mouseButton, ClickType type) {
        if (type == ClickType.CLONE) {
            super.slotClicked(null, slotId, 1, type);
        } else {
            if (slotIn != null) {
                if (type == ClickType.PICKUP) {
                    if (hasAltDown()) {
                        if (Objects.equals(slotIn.container, this.menu.getChestInventory())) {
                            super.slotClicked(slotIn, slotId, 2, type);
                            return;
                        }
                    } else if (hasControlDown() && (Objects.equals(slotIn.container, this.menu.getChestInventory()) || Objects.equals(slotIn.container, this.menu.getPlayerInventory()))) {
                        super.slotClicked(slotIn, slotId, 3, type);
                        return;
                    }
                } else if (type == ClickType.QUICK_MOVE && (Objects.equals(slotIn.container, this.menu.getChestInventory()) || Objects.equals(slotIn.container, this.menu.getPlayerInventory())) && hasControlDown()) {
                    super.slotClicked(slotIn, slotId, 2, type);
                    return;
                }
            }

            if (slotIn != null) super.slotClicked(slotIn, slotId, mouseButton, type);
        }
    }

    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        int index = this.menu.getSwapIndex();
        Slot slot = index >= 0 && index < this.menu.slots.size() ? this.menu.getSlot(index) : null;
        if (slot != null && Objects.equals(slot.container, this.menu.getChestInventory())) {
            RenderSystem.disableDepthTest();
            int xPos = this.leftPos + slot.x;
            int yPos = this.topPos + slot.y;
            RenderSystem.colorMask(true, true, true, false);
            this.fillGradient(matrixStack, xPos, yPos, xPos + 16, yPos + 16, -2130771968, -2130771968);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
        }

    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    public List<ITextComponent> getTooltipFromItem(ItemStack itemStack) {
        List<ITextComponent> tooltip = super.getTooltipFromItem(itemStack);
        Slot slot = this.getSlotUnderMouse();
        if (slot != null && Objects.equals(slot.container, (this.menu).getChestInventory())) {
            long count = (this.menu).getItemCount(slot.getSlotIndex());
            String text = I18n.get("gui.terrible_chest.terrible_chest.count", count);
            tooltip.add(1, new StringTextComponent(text));
        }

        return tooltip;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for(int i = 0; i < SORT_KEYS.length; ++i) {
            if (SORT_KEYS[i].matches(keyCode, scanCode)) {
                super.slotClicked(null, 0, i, ClickType.CLONE);
                return true;
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    static {
        SORT_KEYS = new KeyBinding[]{KeyBindings.SORT_0, KeyBindings.SORT_1, KeyBindings.SORT_2, KeyBindings.SORT_3, KeyBindings.SORT_4, KeyBindings.SORT_5, KeyBindings.SORT_6, KeyBindings.SORT_7, KeyBindings.SORT_8, KeyBindings.SORT_9};
    }

    private static class SinglePage extends TerribleChestScreen {
        public SinglePage(TerribleChestContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
            super(screenContainer, inv, titleIn, null);
            this.imageWidth = 356;
            this.imageHeight = 238;
            this.inventoryLabelX = 98;
            this.inventoryLabelY = this.imageHeight - 93;
        }

        protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.getMinecraft().getTextureManager().bind(TerribleChestScreen.SINGLE_PAGE_TEXTURE);
            blit(matrixStack, this.leftPos, this.topPos, this.getBlitOffset(), 0.0F, 0.0F, this.imageWidth, this.imageHeight, 256, 512);
            super.renderBg(matrixStack, partialTicks, x, y);
        }
    }

    private static class MultiPage extends TerribleChestScreen {
        private final int inventoryRows;

        public MultiPage(TerribleChestContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
            super(screenContainer, inv, titleIn, null);
            this.inventoryRows = Config.COMMON.inventoryRows.get();
            this.imageHeight = 132 + this.inventoryRows * 18;
            this.inventoryLabelY = this.imageHeight - 93;
        }

        protected void init() {
            super.init();
            this.addButton(new ImageButton(this.leftPos + 121, this.topPos + 6, 11, 11, 187, 118, 11, TerribleChestScreen.MULTI_PAGE_TEXTURE, (button) -> Networks.getChannel().sendToServer(new ChangePage((this.menu).getCurrentPage() - 10))));
            this.addButton(new ImageButton(this.leftPos + 134, this.topPos + 6, 7, 11, 183, 96, 11, TerribleChestScreen.MULTI_PAGE_TEXTURE, (button) -> Networks.getChannel().sendToServer(new ChangePage((this.menu).getCurrentPage() - 1))));
            this.addButton(new ImageButton(this.leftPos + 149, this.topPos + 6, 7, 11, 176, 96, 11, TerribleChestScreen.MULTI_PAGE_TEXTURE, (button) -> Networks.getChannel().sendToServer(new ChangePage((this.menu).getCurrentPage() + 1))));
            this.addButton(new ImageButton(this.leftPos + 158, this.topPos + 6, 11, 11, 176, 118, 11, TerribleChestScreen.MULTI_PAGE_TEXTURE, (button) -> Networks.getChannel().sendToServer(new ChangePage((this.menu).getCurrentPage() + 10))));
            this.addButton(new ImageButton(this.leftPos + 181, this.topPos + 8, 20, 20, 176, 56, 20, TerribleChestScreen.MULTI_PAGE_TEXTURE, (button) -> Networks.getChannel().sendToServer(UnlockMaxPage.INSTANCE)));
        }

        protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.getMinecraft().getTextureManager().bind(TerribleChestScreen.MULTI_PAGE_TEXTURE);
            this.blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.inventoryRows * 18 + 35);
            this.blit(matrixStack, this.leftPos, this.topPos + this.inventoryRows * 18 + 35, 0, 143, this.imageWidth, 97);
            this.blit(matrixStack, this.leftPos + this.imageWidth, this.topPos + 3, 176, 0, 33, 56);
            super.renderBg(matrixStack, partialTicks, x, y);
        }

        protected void renderLabels(MatrixStack matrixStack, int x, int y) {
            super.renderLabels(matrixStack, x, y);
            String page = (this.menu).getCurrentPage() + 1 + " / ";
            int pageWidth = this.font.width(page);
            String maxPage = Integer.toString((this.menu).getMaxPage());
            int maxPageWidth = this.font.width(maxPage);
            this.font.draw(matrixStack, page, 169.0F - (float)maxPageWidth - (float)pageWidth, 24.0F, 4210752);
            this.font.draw(matrixStack, maxPage, 169.0F - (float)maxPageWidth, 24.0F, 4210752);
        }
    }
}
