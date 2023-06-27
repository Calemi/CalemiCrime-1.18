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
import com.tm.calemicrime.packet.PacketCalculateRentAcceptorCount;
import com.tm.calemicrime.packet.PacketCalculateRentAcceptorLimit;
import com.tm.calemicrime.packet.PacketRentAcceptor;
import com.tm.calemieconomy.item.ItemWallet;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenRentAcceptor extends ScreenContainerBase<MenuRentAcceptor> {

    private final BlockEntityRentAcceptor rentAcceptor;

    public int typeCount = -1;
    public int typeLimit = -1;

    public String teamName = "";

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

        addRenderableWidget(new SmoothButton(getScreenX() + 8, getScreenY() + 17, 70, "screen.rent_acceptor.btn.payrent", (btn) -> payRent()));
        addRenderableWidget(new SmoothButton(getScreenX() + 98, getScreenY() + 17, 70, "screen.rent_acceptor.btn.stoprent", (btn) -> stopRent()));

        calculateCount();
        calculateLimit();
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (Minecraft.getInstance().level.getGameTime() % 20 == 0) {
            calculateCount();
            calculateLimit();
        }
    }

    private void payRent() {

        if (typeCount == -1 || typeLimit == -1) {
            return;
        }

        CCPacketHandler.INSTANCE.sendToServer(new PacketRentAcceptor("payRent", rentAcceptor.getBlockPos(), 0, 0, "", "", 0));
    }

    private void calculateCount()  {
        CCPacketHandler.INSTANCE.sendToServer(new PacketCalculateRentAcceptorCount(rentAcceptor.rentType));
    }

    private void calculateLimit()  {
        CCPacketHandler.INSTANCE.sendToServer(new PacketCalculateRentAcceptorLimit(rentAcceptor.rentType));
    }

    private void stopRent () {

        rentAcceptor.emptyRentTime();
        CCPacketHandler.INSTANCE.sendToServer(new PacketRentAcceptor("stoprent", rentAcceptor.getBlockPos()));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY) {
        super.renderBg(poseStack, partialTick, mouseX, mouseY);

        //Time Left Bar
        poseStack.pushPose();
        RenderSystem.setShaderTexture(0, textureLocation);
        ScreenRect rectTimeLeft = new ScreenRect(getScreenX() + 8, getScreenY() + 40, MathHelper.scaleInt(rentAcceptor.getRemainingRentSeconds(), rentAcceptor.getMaxRentSeconds(), 161), 7);
        ScreenHelper.drawRect(0, 136, rectTimeLeft, 0);
        poseStack.popPose();
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        super.render(poseStack, mouseX, mouseY, partialTick);

        ScreenHelper.drawCenteredString(poseStack, getScreenX() + getXSize() / 2, getScreenY() - 10, 0, 0xFFFFFF, new TextComponent(rentAcceptor.rentType + ": " + typeCount + " / " + typeLimit));

        if (rentAcceptor.autoPlotReset && rentAcceptor.getTimeUntilPlotReset() > 0 && rentAcceptor.getTimeUntilPlotReset() < rentAcceptor.plotResetTimeSeconds) {
            ScreenHelper.drawCenteredString(poseStack, getScreenX() + getXSize() / 2, getScreenY() - 20, 0, 0xFFFFFF, new TextComponent("Plot will reset in: " + rentAcceptor.getFormattedTime(rentAcceptor.getTimeUntilPlotReset())));
        }

        if (!rentAcceptor.residentTeamName.equals("")) {

            poseStack.pushPose();
            RenderSystem.setShaderTexture(0, textureLocation);
            int nameSize = minecraft.font.width(rentAcceptor.residentTeamName) + 8;
            ScreenRect rectTimeLeft = new ScreenRect(getScreenX() + (getXSize() / 2) - (nameSize / 2), getScreenY() + getYSize(), nameSize, 11);
            ScreenHelper.drawExpandableRect(0, 143, rectTimeLeft, 160, 11, 0);
            poseStack.popPose();

            ScreenHelper.drawCenteredString(poseStack, getScreenX() + getXSize() / 2, getScreenY() + getYSize() + 1, 0, 4210752, new TextComponent(rentAcceptor.residentTeamName));
        }

        MutableComponent currentCurrency = new TextComponent("");

        ItemStack walletStack = CurrencyHelper.getCurrentWallet(Minecraft.getInstance().player);

        if (!walletStack.isEmpty() && walletStack.getItem() instanceof ItemWallet wallet) {
            currentCurrency.append("Wallet: ").append(CurrencyHelper.formatCurrency(wallet.getCurrency(walletStack), true));
        }

        //Bank Currency Tab
        else if (rentAcceptor.getBank() != null) {
            currentCurrency.append("Bank: ").append(CurrencyHelper.formatCurrency(rentAcceptor.getBank().getCurrency(), true));
        }

        else {
            currentCurrency.append("No currency holder found!");
        }

        //Amount to Refill Hover Box
        ScreenRect rectHoverRefill = new ScreenRect(getScreenX() + 8, getScreenY() + 17, 70, 16);

        ScreenHelper.drawHoveringTextBox(poseStack, rectHoverRefill, 0, mouseX, mouseY, 0xFFFFFF,
                currentCurrency,
                new TextComponent("Cost to Refill Time: ").append(CurrencyHelper.formatCurrency(rentAcceptor.getCostToRefillRentTime(), true)));


        //Time Left Hover Box
        ScreenRect rectHoverTimeLeft = new ScreenRect(getScreenX() + 8, getScreenY() + 40, 160, 7);
        ScreenHelper.drawHoveringTextBox(poseStack, rectHoverTimeLeft, 0, mouseX, mouseY, 0xFFFFFF,
                new TextComponent("Remaining: ").append(rentAcceptor.getFormattedTime(rentAcceptor.getRemainingRentSeconds())),
                new TextComponent("Max:        ").append(rentAcceptor.getFormattedTime(rentAcceptor.getMaxRentSeconds())));
    }
}
