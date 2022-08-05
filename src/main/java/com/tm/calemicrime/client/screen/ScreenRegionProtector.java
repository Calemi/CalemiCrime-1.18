package com.tm.calemicrime.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.screen.ScreenBase;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.packet.PacketRegionProtector;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ScreenRegionProtector extends ScreenBase {

    private final BlockEntityRegionProtector regionProtector;

    private EditBox priorityBox;

    private EditBox regionOffsetXBox;
    private EditBox regionOffsetYBox;
    private EditBox regionOffsetZBox;

    private EditBox regionEdgeXBox;
    private EditBox regionEdgeYBox;
    private EditBox regionEdgeZBox;

    private final SmoothButton[] regionRuleSetButtons;

    public ScreenRegionProtector(Player player, InteractionHand hand, BlockEntityRegionProtector regionProtector) {
        super(player, hand);
        this.regionProtector = regionProtector;
        regionRuleSetButtons = new SmoothButton[regionProtector.getRegionRuleSet().ruleSets.length];
    }

    @Override
    protected void init() {
        super.init();

        int rightOffset = 30;
        int editBoxYOffset = 2;
        int editBoxXSpace = 45;
        int editBoxYSpace = 30;

        int btnXOffset = -60;
        int btnYOffset = -47;
        int btnYSpace = 20;

        priorityBox = initField(regionProtector.getPriority(), rightOffset, editBoxYOffset - editBoxYSpace);

        regionOffsetXBox = initField(regionProtector.getRegionOffset().x, rightOffset, editBoxYOffset);
        regionOffsetYBox = initField(regionProtector.getRegionOffset().y, rightOffset + editBoxXSpace, editBoxYOffset);
        regionOffsetZBox = initField(regionProtector.getRegionOffset().z, rightOffset + editBoxXSpace * 2, editBoxYOffset);

        regionEdgeXBox = initField(regionProtector.getRegionEdge().x, rightOffset, editBoxYOffset + editBoxYSpace);
        regionEdgeYBox = initField(regionProtector.getRegionEdge().y, rightOffset + editBoxXSpace, editBoxYOffset + editBoxYSpace);
        regionEdgeZBox = initField(regionProtector.getRegionEdge().z, rightOffset + editBoxXSpace * 2, editBoxYOffset + editBoxYSpace);

        for (int i = 0; i < regionRuleSetButtons.length; i++) {
            final int fi = i;
            regionRuleSetButtons[i] = addRenderableWidget(new SmoothButton(getScreenX() + btnXOffset, getScreenY() + btnYOffset + (btnYSpace * i), 50, getRuleButtonKey(i), (btn) -> toggleRule(fi)));
        }
    }

    private EditBox initField (int value, int x, int y) {

        if (minecraft != null) {
            EditBox editBox = new EditBox(minecraft.font, getScreenX() + x - 20, getScreenY() + y - 7, 40, 12, new TextComponent(""));
            addWidget(editBox);
            editBox.setMaxLength(15);
            editBox.setValue("" + value);
            return editBox;
        }

        return null;
    }

    private String getRuleButtonKey(int ruleSetIndex) {

        RegionRuleSet.RuleOverrideType ruleOverrideType = regionProtector.getRegionRuleSet().ruleSets[ruleSetIndex];

        return switch (ruleOverrideType) {
            case PREVENT -> "screen.regionprotector.btn.rule.prevent";
            case ALLOW -> "screen.regionprotector.btn.rule.allow";
            case OFF -> "screen.regionprotector.btn.rule.off";
        };
    }

    private void confirmEditBoxes() {

        int priority = parseCoordinate(priorityBox.getValue());

        int offsetX = parseCoordinate(regionOffsetXBox.getValue());
        int offsetY = parseCoordinate(regionOffsetYBox.getValue());
        int offsetZ = parseCoordinate(regionOffsetZBox.getValue());

        int edgeX = parseCoordinate(regionEdgeXBox.getValue());
        int edgeY = parseCoordinate(regionEdgeYBox.getValue());
        int edgeZ = parseCoordinate(regionEdgeZBox.getValue());

        priorityBox.setValue("" + priority);

        regionOffsetXBox.setValue("" + offsetX);
        regionOffsetYBox.setValue("" + offsetY);
        regionOffsetZBox.setValue("" + offsetZ);

        regionEdgeXBox.setValue("" + edgeX);
        regionEdgeYBox.setValue("" + edgeY);
        regionEdgeZBox.setValue("" + edgeZ);

        BlockPos offset = new BlockPos(offsetX, offsetY, offsetZ);
        BlockPos edge = new BlockPos(edgeX, edgeY, edgeZ);

        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("synclocations", regionProtector.getBlockPos(), offset, edge));
        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("syncpriority", regionProtector.getBlockPos(), priority));
    }

    private int parseCoordinate(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException numberformatexception) {
            return 0;
        }
    }

    private void toggleRule(int ruleSetIndex) {

        byte ruleOverrideIndex = regionProtector.getRegionRuleSet().ruleSets[ruleSetIndex].getIndex();

        ruleOverrideIndex++;
        ruleOverrideIndex %= 3;

        regionProtector.getRegionRuleSet().ruleSets[ruleSetIndex] = RegionRuleSet.RuleOverrideType.fromIndex(ruleOverrideIndex);
        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("syncrule", regionProtector.getBlockPos(), ruleSetIndex, ruleOverrideIndex));
    }

    @Override
    public void tick() {
        super.tick();

        regionOffsetXBox.tick();
        regionOffsetYBox.tick();
        regionOffsetZBox.tick();

        regionEdgeXBox.tick();
        regionEdgeYBox.tick();
        regionEdgeZBox.tick();

        priorityBox.tick();

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

        regionOffsetXBox.render(poseStack, mouseX, mouseY, 0);
        regionOffsetYBox.render(poseStack, mouseX, mouseY, 0);
        regionOffsetZBox.render(poseStack, mouseX, mouseY, 0);

        regionEdgeXBox.render(poseStack, mouseX, mouseY, 0);
        regionEdgeYBox.render(poseStack, mouseX, mouseY, 0);
        regionEdgeZBox.render(poseStack, mouseX, mouseY, 0);

        priorityBox.render(poseStack, mouseX, mouseY, 0);

        int editBoxYOffset = 11;

        TranslatableComponent priorityText = new TranslatableComponent("screen.regionprotector.txt.priority");
        minecraft.font.draw(poseStack, priorityText, priorityBox.x, priorityBox.y - editBoxYOffset, 0xFFFFFF);

        TranslatableComponent regionOffsetText = new TranslatableComponent("screen.regionprotector.txt.regionoffset");
        minecraft.font.draw(poseStack, regionOffsetText, regionOffsetXBox.x, regionOffsetXBox.y - editBoxYOffset, 0xFFFFFF);

        TranslatableComponent regionEdgeText = new TranslatableComponent("screen.regionprotector.txt.regionedge");
        minecraft.font.draw(poseStack, regionEdgeText, regionEdgeXBox.x, regionEdgeXBox.y - editBoxYOffset, 0xFFFFFF);

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
