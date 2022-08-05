package com.tm.calemicrime.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.MathHelper;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.screen.ScreenContainerBase;
import com.tm.calemicore.util.screen.ScreenRect;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.menu.MenuRentAcceptor;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.packet.PacketRentAcceptor;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.util.helper.ScreenTabs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenRentAcceptor extends ScreenContainerBase<MenuRentAcceptor> {

    private final BlockEntityRentAcceptor rentAcceptor;

    public ScreenRentAcceptor(MenuRentAcceptor menu, Inventory playerInv, Component useless) {
        super(menu, playerInv, menu.getBlockEntity().getDisplayName());
        textureLocation = new ResourceLocation(CCReference.MOD_ID, "textures/gui/rent_acceptor.png");
        rentAcceptor = (BlockEntityRentAcceptor) getMenu().getBlockEntity();
        inventoryLabelY = Integer.MAX_VALUE;
        imageHeight = 136;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(new SmoothButton(getScreenX() + 62, getScreenY() + 17, 52, "screen.rent_acceptor.btn.payrent", (btn) -> payRent()));
    }

    private void payRent () {

        ItemStack walletStack = rentAcceptor.getItem(0);

        //TRY WALLET
        if (walletStack.getItem() instanceof ItemWallet wallet) {

            int walletCurrency = wallet.getCurrency(walletStack);

            if (walletCurrency >= rentAcceptor.getCostToRefillRentTime()) {

                wallet.withdrawCurrency(walletStack, rentAcceptor.getCostToRefillRentTime());
                rentAcceptor.refillRentTime();

                CCPacketHandler.INSTANCE.sendToServer(new PacketRentAcceptor("refillrentwallet", rentAcceptor.getBlockPos(), walletCurrency, wallet.getCurrency(walletStack)));
            }
        }

        else if (rentAcceptor.getBank() != null) {

            int bankCurrency = rentAcceptor.getBank().getCurrency();

            if (bankCurrency >= rentAcceptor.getCostToRefillRentTime()) {

                rentAcceptor.getBank().withdrawCurrency(rentAcceptor.getCostToRefillRentTime());
                rentAcceptor.refillRentTime();

                CCPacketHandler.INSTANCE.sendToServer(new PacketRentAcceptor("refillrentwallet", rentAcceptor.getBlockPos(), 0, rentAcceptor.getBank().getCurrency()));
            }
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        if (rentAcceptor.getBank() != null) {
            ScreenTabs.addCurrencyTab(poseStack, getScreenX(), getScreenY() + 5, mouseX, mouseY, rentAcceptor.getBank());
        }

        poseStack.pushPose();

        RenderSystem.setShaderTexture(0, textureLocation);
        ScreenRect rectTimeLeft = new ScreenRect(getScreenX() + 8, getScreenY() + 40, MathHelper.scaleInt(rentAcceptor.getRentTicksRemaining(), rentAcceptor.getMaxRentTicks(), 160), 7);
        ScreenHelper.drawExpandableRect(0, 136, rectTimeLeft, 160, 7, 0);

        poseStack.popPose();

        ScreenRect rectHoverRefill = new ScreenRect(getScreenX() + 62, getScreenY() + 17, 52, 16);
        ScreenHelper.drawHoveringTextBox(poseStack, rectHoverRefill, 0, mouseX, mouseY, 0xFFFFFF, new TextComponent("Cost to Refill Time: " + rentAcceptor.getCostToRefillRentTime()));

        ScreenRect rectHoverTimeLeft = new ScreenRect(getScreenX() + 8, getScreenY() + 40, 160, 7);
        ScreenHelper.drawHoveringTextBox(poseStack, rectHoverTimeLeft, 0, mouseX, mouseY, 0xFFFFFF, new TextComponent("Time Left: " + rentAcceptor.getFormattedTimeLeft()));
    }
}