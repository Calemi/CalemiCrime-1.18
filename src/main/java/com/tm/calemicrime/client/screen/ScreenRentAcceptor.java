package com.tm.calemicrime.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicore.util.helper.MathHelper;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.screen.ScreenContainerBase;
import com.tm.calemicore.util.screen.ScreenRect;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.file.RentAcceptorTypesFile;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.menu.MenuRentAcceptor;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.packet.PacketRentAcceptor;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import com.tm.calemieconomy.util.helper.ScreenTabs;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import dev.ftb.mods.ftbteams.data.ClientTeamManager;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenRentAcceptor extends ScreenContainerBase<MenuRentAcceptor> {

    ClientTeamManager manager = ClientTeamManager.INSTANCE;
    private final BlockEntityRentAcceptor rentAcceptor;

    private int typeCount = 0;
    private int typeLimit = 0;

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

        addRenderableWidget(new SmoothButton(getScreenX() + 53, getScreenY() + 17, 52, "screen.rent_acceptor.btn.payrent", (btn) -> payRent()));
        addRenderableWidget(new SmoothButton(getScreenX() + 117, getScreenY() + 17, 52, "screen.rent_acceptor.btn.stoprent", (btn) -> stopRent()));

        typeCount = countTypes();
        typeLimit = RentAcceptorTypesFile.getLimit(rentAcceptor.getRentAcceptorType());
    }

    private void payRent () {

        if (manager == null) {
            return;
        }

        typeCount = countTypes();
        typeLimit = RentAcceptorTypesFile.getLimit(rentAcceptor.getRentAcceptorType());

        LogHelper.log(CCReference.MOD_NAME, "Current count of type: " + typeCount);
        LogHelper.log(CCReference.MOD_NAME, "Limit of type: " + typeLimit);

        if (rentAcceptor.getRemainingRentTime() <= 0 && typeCount >= typeLimit) {
            Minecraft.getInstance().player.sendMessage(new TextComponent(ChatFormatting.RED + "You have reached your limit for this rent type!"), Util.NIL_UUID);
            Minecraft.getInstance().player.sendMessage(new TextComponent(ChatFormatting.RED + "Limit for [" + rentAcceptor.getRentAcceptorType() + "] is " + typeLimit), Util.NIL_UUID);
            return;
        }

        ItemStack walletStack = rentAcceptor.getItem(0);

        //TRY WALLET
        if (walletStack.getItem() instanceof ItemWallet wallet) {

            int walletCurrency = wallet.getCurrency(walletStack);

            if (walletCurrency >= rentAcceptor.getCostToRefillRentTime()) {

                if (rentAcceptor.getRemainingRentTime() <= 0) {
                    typeCount++;
                }

                wallet.withdrawCurrency(walletStack, rentAcceptor.getCostToRefillRentTime());
                rentAcceptor.refillRentTime();

                CCPacketHandler.INSTANCE.sendToServer(new PacketRentAcceptor("refillrentwallet", rentAcceptor.getBlockPos(), 0, 0, "", wallet.getCurrency(walletStack), 0));
            }
        }

        else if (rentAcceptor.getBank() != null) {

            int bankCurrency = rentAcceptor.getBank().getCurrency();

            if (bankCurrency >= rentAcceptor.getCostToRefillRentTime()) {

                if (rentAcceptor.getRemainingRentTime() <= 0) {
                    typeCount++;
                }

                rentAcceptor.getBank().withdrawCurrency(rentAcceptor.getCostToRefillRentTime());
                rentAcceptor.refillRentTime();

                CCPacketHandler.INSTANCE.sendToServer(new PacketRentAcceptor("refillrentbank", rentAcceptor.getBlockPos(), 0, 0, "", 0, rentAcceptor.getBank().getCurrency()));
            }
        }
    }

    private int countTypes()  {

        int count = 0;

        for (BlockEntityRentAcceptor rentAcceptorList : BlockEntityRentAcceptor.getRentAcceptors()) {

            if (rentAcceptorList.getRentAcceptorType().equals(rentAcceptor.getRentAcceptorType())) {

                if (rentAcceptorList.getResidentTeamClient(manager) == manager.selfTeam) {
                    count++;
                }
            }
        }

        return count;
    }

    private void stopRent () {

        if (rentAcceptor.getRemainingRentTime() > 0) {
            typeCount--;
        }

        rentAcceptor.emptyRentTime();
        CCPacketHandler.INSTANCE.sendToServer(new PacketRentAcceptor("stoprent", rentAcceptor.getBlockPos()));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        //Time Left Bar
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, textureLocation);
        ScreenRect rectTimeLeft = new ScreenRect(getScreenX() + 8, getScreenY() + 40, MathHelper.scaleInt(rentAcceptor.getRemainingRentTime(), rentAcceptor.getMaxRentTime(), 161), 7);
        ScreenHelper.drawRect(0, 136, rectTimeLeft, 0);
        poseStack.popPose();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        ScreenHelper.drawCenteredString(poseStack, getScreenX() + getXSize() / 2, getScreenY() - 10, 0, 0xFFFFFF, new TextComponent(rentAcceptor.getRentAcceptorType() + ": " + typeCount + " / " + typeLimit));

        //Bank Currency Tab
        if (rentAcceptor.getBank() != null) {
            ScreenTabs.addCurrencyTab(poseStack, getScreenX(), getScreenY() + 5, mouseX, mouseY, rentAcceptor.getBank());
        }

        if (manager != null) {

            //Team Tab
            ClientTeam residentTeam = rentAcceptor.getResidentTeamClient(manager);

            if (residentTeam != null) {

                poseStack.pushPose();
                RenderSystem.setShaderTexture(0, textureLocation);
                int nameSize = minecraft.font.width(residentTeam.getDisplayName()) + 8;
                ScreenRect rectTimeLeft = new ScreenRect(getScreenX() + (getXSize() / 2) - (nameSize / 2), getScreenY() + getYSize(), nameSize, 11);
                ScreenHelper.drawExpandableRect(0, 143, rectTimeLeft, 160, 11, 0);
                poseStack.popPose();

                ScreenHelper.drawCenteredString(poseStack, getScreenX() + getXSize() / 2, getScreenY() + getYSize() + 1, 0, 4210752, new TextComponent(residentTeam.getDisplayName()));
            }
        }

        //Amount to Refill Hover Box
        ScreenRect rectHoverRefill = new ScreenRect(getScreenX() + 62, getScreenY() + 17, 52, 16);
        ScreenHelper.drawHoveringTextBox(poseStack, rectHoverRefill, 0, mouseX, mouseY, 0xFFFFFF, new TextComponent("Cost to Refill Time: ").append(CurrencyHelper.formatCurrency(rentAcceptor.getCostToRefillRentTime())));

        //Time Left Hover Box
        ScreenRect rectHoverTimeLeft = new ScreenRect(getScreenX() + 8, getScreenY() + 40, 160, 7);
        ScreenHelper.drawHoveringTextBox(poseStack, rectHoverTimeLeft, 0, mouseX, mouseY, 0xFFFFFF,
                new TextComponent("Remaining: ").append(rentAcceptor.getFormattedTime(rentAcceptor.getRemainingRentTime())),
                new TextComponent("Max:        ").append(rentAcceptor.getFormattedTime(rentAcceptor.getMaxRentTime())));
    }
}
