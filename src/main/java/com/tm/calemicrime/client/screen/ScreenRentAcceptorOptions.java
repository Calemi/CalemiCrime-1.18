package com.tm.calemicrime.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.screen.ScreenBase;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemicrime.blockentity.BlockEntityRentAcceptor;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.packet.PacketRegionProtector;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenRentAcceptorOptions extends ScreenBase {

    private final BlockEntityRentAcceptor rentAcceptor;

    private final SmoothButton[] regionRuleSetButtons;

    public ScreenRentAcceptorOptions(Player player, InteractionHand hand, BlockEntityRentAcceptor rentAcceptor) {
        super(player, hand);
        this.rentAcceptor = rentAcceptor;
        regionRuleSetButtons = new SmoothButton[rentAcceptor.getRegionRuleSetOverride().ruleSets.length];
    }

    @Override
    protected void init() {
        super.init();

        int btnXOffset = -60;
        int btnYOffset = -47;
        int btnYSpace = 20;

        for (int i = 0; i < regionRuleSetButtons.length; i++) {
            final int fi = i;
            regionRuleSetButtons[i] = addRenderableWidget(new SmoothButton(getScreenX() + btnXOffset, getScreenY() + btnYOffset + (btnYSpace * i), 50, getRuleButtonKey(i), (btn) -> toggleRule(fi)));
        }
    }

    private String getRuleButtonKey(int ruleSetIndex) {

        RegionRuleSet.RuleOverrideType ruleOverrideType = rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex];

        return switch (ruleOverrideType) {
            case PREVENT -> "screen.regionprotector.btn.rule.prevent";
            case ALLOW -> "screen.regionprotector.btn.rule.allow";
            case OFF -> "screen.regionprotector.btn.rule.off";
        };
    }

    private void confirmEditBoxes() {

    }

    private int parseCoordinate(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException numberformatexception) {
            return 0;
        }
    }

    private void toggleRule(int ruleSetIndex) {

        byte ruleOverrideIndex = rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex].getIndex();

        ruleOverrideIndex++;
        ruleOverrideIndex %= 3;

        rentAcceptor.getRegionRuleSetOverride().ruleSets[ruleSetIndex] = RegionRuleSet.RuleOverrideType.fromIndex(ruleOverrideIndex);
        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("syncrule", rentAcceptor.getBlockPos(), ruleSetIndex, ruleOverrideIndex));
    }

    @Override
    public void tick() {
        super.tick();

        regionRuleSetButtons[0].setMessage(new TranslatableComponent(getRuleButtonKey(0)));
        regionRuleSetButtons[1].setMessage(new TranslatableComponent(getRuleButtonKey(1)));
        regionRuleSetButtons[2].setMessage(new TranslatableComponent(getRuleButtonKey(2)));

        regionRuleSetButtons[3].setMessage(new TranslatableComponent(getRuleButtonKey(3)));
        regionRuleSetButtons[4].setMessage(new TranslatableComponent(getRuleButtonKey(4)));
    }

    @Override
    protected void drawGuiBackground(PoseStack poseStack, int mouseX, int mouseY) {}

    @Override
    protected void drawGuiForeground(PoseStack poseStack, int mouseX, int mouseY) {

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
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        //Escape Key
        if (keyCode == 256) {
            minecraft.player.closeContainer();
        }

        //Enter Key
        else if (keyCode == 257) {
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
        return true;
    }

    @Override
    public boolean isPauseScreen () {
        return false;
    }
}