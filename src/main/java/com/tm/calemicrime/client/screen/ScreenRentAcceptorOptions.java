package com.tm.calemicrime.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.screen.ScreenBase;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.packet.PacketRentAcceptor;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenRentAcceptorOptions extends ScreenBase {

    private final BlockEntityRentAcceptor rentAcceptor;

    private final SmoothButton[] regionRuleSetButtons;

    private EditBox fileKeyBox;
    private EditBox maxRentTimeBox;
    private EditBox costToFillRentTimeBox;
    private EditBox typeBox;

    private SmoothButton autoResetPlotBtn;
    private EditBox plotResetTimeBox;

    public ScreenRentAcceptorOptions(Player player, InteractionHand hand, BlockEntityRentAcceptor rentAcceptor) {
        super(player, hand);
        this.rentAcceptor = rentAcceptor;
        regionRuleSetButtons = new SmoothButton[rentAcceptor.regionRuleSetOverride.ruleSets.length];
    }

    @Override
    protected void init() {
        super.init();

        int btnXOffset = -60;
        int btnYOffset = -58;
        int btnYSpace = 20;

        int editBoxXOffset = 30;
        int editBoxYOffset = -40;
        int editBoxYSpace = 30;

        for (int i = 0; i < regionRuleSetButtons.length; i++) {
            final int fi = i;
            regionRuleSetButtons[i] = addRenderableWidget(new SmoothButton(getScreenX() + btnXOffset, getScreenY() + btnYOffset + (btnYSpace * i), 50, getRuleButtonKey(i), (btn) -> toggleRule(fi)));
        }

        fileKeyBox = initField(rentAcceptor.fileKey, editBoxXOffset, editBoxYOffset);
        maxRentTimeBox = initField(rentAcceptor.maxRentHours, editBoxXOffset, editBoxYOffset + editBoxYSpace);
        costToFillRentTimeBox = initField(rentAcceptor.costToFillRentTime, editBoxXOffset, editBoxYOffset + (editBoxYSpace * 2));
        typeBox = initField(rentAcceptor.rentType, editBoxXOffset, editBoxYOffset + (editBoxYSpace * 3));

        autoResetPlotBtn = addRenderableWidget(new SmoothButton(getScreenX() + btnXOffset, getScreenY() + btnYOffset + (btnYSpace * 6), 50, getAutoResetPlotButtonKey(), (btn) -> toggleAutoPlotReset()));
        plotResetTimeBox = initField(rentAcceptor.plotResetTimeSeconds, editBoxXOffset, editBoxYOffset + (editBoxYSpace * 4));
    }

    private EditBox initField (Object value, int x, int y) {

        if (minecraft != null) {
            EditBox editBox = new EditBox(minecraft.font, getScreenX() + x - 20, getScreenY() + y - 7, 100, 12, new TextComponent(""));
            addWidget(editBox);
            editBox.setMaxLength(30);
            editBox.setValue("" + value);
            return editBox;
        }

        return null;
    }

    private String getRuleButtonKey(int ruleSetIndex) {

        RegionRuleSet.RuleOverrideType ruleOverrideType = rentAcceptor.regionRuleSetOverride.ruleSets[ruleSetIndex];

        return switch (ruleOverrideType) {
            case PREVENT -> "screen.regionprotector.btn.rule.prevent";
            case ALLOW -> "screen.regionprotector.btn.rule.allow";
            case OFF -> "screen.regionprotector.btn.rule.off";
        };
    }

    private String getAutoResetPlotButtonKey() {
        return rentAcceptor.autoPlotReset ? "screen.regionprotector.btn.true" : "screen.regionprotector.btn.false";
    }

    private void confirmEditBoxes() {

        int maxRentHours = parseInteger(maxRentTimeBox.getValue());
        long costToFill = parseLong(costToFillRentTimeBox.getValue());

        maxRentTimeBox.setValue("" + maxRentHours);
        costToFillRentTimeBox.setValue("" + costToFill);

        int plotResetTime = parseInteger(plotResetTimeBox.getValue());

        CCPacketHandler.INSTANCE.sendToServer(new PacketRentAcceptor("syncoptions", rentAcceptor.getBlockPos(), maxRentHours, costToFill, typeBox.getValue(), fileKeyBox.getValue(), plotResetTime));
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException numberformatexception) {
            return 0;
        }
    }

    private long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException numberformatexception) {
            return 0;
        }
    }


    private void toggleRule(int ruleSetIndex) {

        byte ruleOverrideIndex = rentAcceptor.regionRuleSetOverride.ruleSets[ruleSetIndex].getIndex();

        ruleOverrideIndex++;
        ruleOverrideIndex %= 3;

        rentAcceptor.regionRuleSetOverride.ruleSets[ruleSetIndex] = RegionRuleSet.RuleOverrideType.fromIndex(ruleOverrideIndex);
        CCPacketHandler.INSTANCE.sendToServer(new PacketRentAcceptor("syncrule", rentAcceptor.getBlockPos(), ruleSetIndex, ruleOverrideIndex));
    }

    private void toggleAutoPlotReset() {
        rentAcceptor.autoPlotReset = !rentAcceptor.autoPlotReset;
        CCPacketHandler.INSTANCE.sendToServer(new PacketRentAcceptor("syncautoplotreset", rentAcceptor.getBlockPos(), rentAcceptor.autoPlotReset));
    }

    @Override
    public void tick() {
        super.tick();

        regionRuleSetButtons[0].setMessage(new TranslatableComponent(getRuleButtonKey(0)));
        regionRuleSetButtons[1].setMessage(new TranslatableComponent(getRuleButtonKey(1)));
        regionRuleSetButtons[2].setMessage(new TranslatableComponent(getRuleButtonKey(2)));

        regionRuleSetButtons[3].setMessage(new TranslatableComponent(getRuleButtonKey(3)));
        regionRuleSetButtons[4].setMessage(new TranslatableComponent(getRuleButtonKey(4)));
        regionRuleSetButtons[5].setMessage(new TranslatableComponent(getRuleButtonKey(5)));

        maxRentTimeBox.tick();
        costToFillRentTimeBox.tick();
        typeBox.tick();
        fileKeyBox.tick();
        plotResetTimeBox.tick();

        autoResetPlotBtn.setMessage(new TranslatableComponent(getAutoResetPlotButtonKey()));
    }

    @Override
    protected void drawGuiBackground(PoseStack poseStack, int mouseX, int mouseY) {}

    @Override
    protected void drawGuiForeground(PoseStack poseStack, int mouseX, int mouseY) {

        fileKeyBox.render(poseStack, mouseX, mouseY, 0);
        maxRentTimeBox.render(poseStack, mouseX, mouseY, 0);
        costToFillRentTimeBox.render(poseStack, mouseX, mouseY, 0);
        typeBox.render(poseStack, mouseX, mouseY, 0);
        plotResetTimeBox.render(poseStack, mouseX, mouseY, 0);

        int editBoxYOffset = 11;

        TranslatableComponent fileKeyText = new TranslatableComponent("screen.rent_acceptor.txt.filekey");
        minecraft.font.draw(poseStack, fileKeyText, fileKeyBox.x, fileKeyBox.y - editBoxYOffset, 0xFFFFFF);

        TranslatableComponent maxRentTicksText = new TranslatableComponent("screen.rent_acceptor.txt.maxrenttime");
        minecraft.font.draw(poseStack, maxRentTicksText, maxRentTimeBox.x, maxRentTimeBox.y - editBoxYOffset, 0xFFFFFF);

        TranslatableComponent costPerHourText = new TranslatableComponent("screen.rent_acceptor.txt.costtofillrenttime");
        minecraft.font.draw(poseStack, costPerHourText, costToFillRentTimeBox.x, costToFillRentTimeBox.y - editBoxYOffset, 0xFFFFFF);

        TranslatableComponent typeText = new TranslatableComponent("screen.rent_acceptor.txt.type");
        minecraft.font.draw(poseStack, typeText, typeBox.x, typeBox.y - editBoxYOffset, 0xFFFFFF);

        TextComponent plotResetTime = new TextComponent("Plot Reset Time (Seconds)");
        minecraft.font.draw(poseStack, plotResetTime, plotResetTimeBox.x, plotResetTimeBox.y - editBoxYOffset, 0xFFFFFF);

        int buttonOffset = 4;

        TranslatableComponent blockBreakingText = new TranslatableComponent("screen.regionprotector.txt.rule.blockbreaking");
        minecraft.font.draw(poseStack, blockBreakingText, regionRuleSetButtons[0].x - minecraft.font.width(blockBreakingText) - buttonOffset, regionRuleSetButtons[0].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent blockPlacingText = new TranslatableComponent("screen.regionprotector.txt.rule.blockplacing");
        minecraft.font.draw(poseStack, blockPlacingText, regionRuleSetButtons[1].x - minecraft.font.width(blockPlacingText) - buttonOffset, regionRuleSetButtons[1].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent blockUsingText = new TranslatableComponent("screen.regionprotector.txt.rule.blockusing");
        minecraft.font.draw(poseStack, blockUsingText, regionRuleSetButtons[2].x - minecraft.font.width(blockUsingText) - buttonOffset, regionRuleSetButtons[2].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent entityHurtingText = new TranslatableComponent("screen.regionprotector.txt.rule.entityhurting");
        minecraft.font.draw(poseStack, entityHurtingText, regionRuleSetButtons[3].x - minecraft.font.width(entityHurtingText) - buttonOffset, regionRuleSetButtons[3].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent entityInteractingText = new TranslatableComponent("screen.regionprotector.txt.rule.entityinteracting");
        minecraft.font.draw(poseStack, entityInteractingText, regionRuleSetButtons[4].x - minecraft.font.width(entityInteractingText) - buttonOffset, regionRuleSetButtons[4].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent pvpText = new TranslatableComponent("screen.regionprotector.txt.rule.pvp");
        minecraft.font.draw(poseStack, pvpText, regionRuleSetButtons[4].x - minecraft.font.width(pvpText) - buttonOffset, regionRuleSetButtons[5].y + buttonOffset, 0xFFFFFF);

        TextComponent autoPlotResetText = new TextComponent("Auto Plot Reset");
        minecraft.font.draw(poseStack, autoPlotResetText, autoResetPlotBtn.x - minecraft.font.width(autoPlotResetText) - buttonOffset, autoResetPlotBtn.y + buttonOffset, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        //Enter Key
        if (keyCode == 257) {
            confirmEditBoxes();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected int getGuiSizeX() {
        return 0;
    }

    @Override
    protected int getGuiSizeY() {
        return 0;
    }

    @Override
    protected boolean canCloseWithInvKey() {
        return !typeBox.isFocused() && !fileKeyBox.isFocused();
    }

    @Override
    public boolean isPauseScreen () {
        return false;
    }
}
