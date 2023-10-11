package com.tm.calemicrime.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.screen.ScreenBase;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.packet.PacketRegionProtector;
import com.tm.calemicrime.util.RegionProfile;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Date;

@OnlyIn(Dist.CLIENT)
public class ScreenRegionProtectorOptions extends ScreenBase {

    private final BlockEntityRegionProtector regionProtector;

    private final SmoothButton[] regionRuleSetBtns;

    private EditBox priorityBox;
    private SmoothButton globalBtn;

    private EditBox regionOffsetXBox;
    private EditBox regionOffsetYBox;
    private EditBox regionOffsetZBox;

    private EditBox regionSizeXBox;
    private EditBox regionSizeYBox;
    private EditBox regionSizeZBox;

    private SmoothButton regionTypeBtn;

    public ScreenRegionProtectorOptions(Player player, InteractionHand hand, BlockEntityRegionProtector regionProtector) {
        super(player, hand);
        this.regionProtector = regionProtector;
        regionRuleSetBtns = new SmoothButton[regionProtector.profile.getRuleSet().ruleSets.length];
    }

    @Override
    protected void init() {
        super.init();

        int btnXOffset = -60;
        int btnYOffset = -58;
        int btnYSpace = 20;

        int editBoxXOffset = 30;
        int editBoxYOffset = -9;
        int editBoxXSpace = 45;
        int editBoxYSpace = 30;

        regionTypeBtn = addRenderableWidget(new SmoothButton(getScreenX(), getScreenY() - 80, 75, getRegionTypeButtonKey(), (btn) -> toggleRegionType()));

        for (int i = 0; i < regionRuleSetBtns.length; i++) {
            final int fi = i;
            regionRuleSetBtns[i] = addRenderableWidget(new SmoothButton(getScreenX() + btnXOffset, getScreenY() + btnYOffset + (btnYSpace * i), 50, getRuleButtonKey(i), (btn) -> toggleRule(fi)));
        }

        priorityBox = initField(regionProtector.profile.getPriority(), editBoxXOffset, editBoxYOffset - editBoxYSpace);
        globalBtn = addRenderableWidget(new SmoothButton(getScreenX() + editBoxXOffset - 21, getScreenY() + editBoxYOffset - 7, 50, getGlobalButtonKey(), (btn) -> toggleGlobal()));

        regionOffsetXBox = initField(regionProtector.profile.getOffset().x, editBoxXOffset, editBoxYOffset + editBoxYSpace);
        regionOffsetYBox = initField(regionProtector.profile.getOffset().y, editBoxXOffset + editBoxXSpace, editBoxYOffset + editBoxYSpace);
        regionOffsetZBox = initField(regionProtector.profile.getOffset().z, editBoxXOffset + editBoxXSpace * 2, editBoxYOffset + editBoxYSpace);

        regionSizeXBox = initField(regionProtector.profile.getSize().x, editBoxXOffset, editBoxYOffset + editBoxYSpace * 2);
        regionSizeYBox = initField(regionProtector.profile.getSize().y, editBoxXOffset + editBoxXSpace, editBoxYOffset + editBoxYSpace * 2);
        regionSizeZBox = initField(regionProtector.profile.getSize().z, editBoxXOffset + editBoxXSpace * 2, editBoxYOffset + editBoxYSpace * 2);

        SmoothButton savePlotBtn = addRenderableWidget(new SmoothButton((getScreenX() - 35) - 40, getScreenY() + 65, 70, "screen.regionprotector.btn.saveplot", (btn) -> savePlot()));
        SmoothButton loadPlotBtn = addRenderableWidget(new SmoothButton((getScreenX() - 35) + 40, getScreenY() + 65, 70, "screen.regionprotector.btn.loadplot", (btn) -> loadPlot()));
    }

    private EditBox initField (Object value, int x, int y) {
        return initField(value, x, y, 40);
    }

    private EditBox initField (Object value, int x, int y, int width) {

        if (minecraft != null) {
            EditBox editBox = new EditBox(minecraft.font, getScreenX() + x - 20, getScreenY() + y - 7, width, 12, new TextComponent(""));
            addWidget(editBox);
            editBox.setMaxLength(15);
            editBox.setValue("" + value);
            return editBox;
        }

        return null;
    }

    private String getRuleButtonKey(int ruleSetIndex) {
        RegionRuleSet.RuleOverrideType ruleOverrideType = regionProtector.profile.getRuleSet().ruleSets[ruleSetIndex];
        return "screen.regionprotector.btn.rule." + ruleOverrideType.getName();
    }

    private String getGlobalButtonKey() {
        return regionProtector.profile.isGlobal() ? "screen.regionprotector.btn.true" : "screen.regionprotector.btn.false";
    }

    private String getRegionTypeButtonKey() {
        return "screen.regionprotector.btn.regiontype." + regionProtector.profile.getType().getName();
    }

    private void confirmEditBoxes() {

        int priority = parseInteger(priorityBox.getValue());

        int offsetX = parseInteger(regionOffsetXBox.getValue());
        int offsetY = parseInteger(regionOffsetYBox.getValue());
        int offsetZ = parseInteger(regionOffsetZBox.getValue());

        int edgeX = parseInteger(regionSizeXBox.getValue());
        int edgeY = parseInteger(regionSizeYBox.getValue());
        int edgeZ = parseInteger(regionSizeZBox.getValue());

        priorityBox.setValue("" + priority);

        regionOffsetXBox.setValue("" + offsetX);
        regionOffsetYBox.setValue("" + offsetY);
        regionOffsetZBox.setValue("" + offsetZ);

        regionSizeXBox.setValue("" + edgeX);
        regionSizeYBox.setValue("" + edgeY);
        regionSizeZBox.setValue("" + edgeZ);

        BlockPos offset = new BlockPos(offsetX, offsetY, offsetZ);
        BlockPos edge = new BlockPos(edgeX, edgeY, edgeZ);

        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("synclocations", regionProtector.getBlockPos(), offset, edge));
        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("syncpriority", regionProtector.getBlockPos(), priority));
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException numberformatexception) {
            return 0;
        }
    }

    private void toggleRule(int ruleSetIndex) {

        byte ruleOverrideIndex = regionProtector.profile.getRuleSet().ruleSets[ruleSetIndex].getIndex();

        ruleOverrideIndex++;
        ruleOverrideIndex %= 3;

        regionProtector.profile.getRuleSet().ruleSets[ruleSetIndex] = RegionRuleSet.RuleOverrideType.fromIndex(ruleOverrideIndex);
        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("syncrule", regionProtector.getBlockPos(), ruleSetIndex, ruleOverrideIndex));
    }

    private void toggleGlobal() {

        final boolean newValue = !regionProtector.profile.isGlobal();

        regionProtector.profile.setGlobal(newValue);
        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("syncglobal", regionProtector.getBlockPos(), newValue, 0));
    }

    private void toggleRegionType() {

        int regionTypeIndex = regionProtector.profile.getType().getIndex();

        regionTypeIndex++;
        regionTypeIndex %= 3;

        regionProtector.profile.setType(RegionProfile.Type.fromIndex(regionTypeIndex));
        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("syncregiontype", regionProtector.getBlockPos(), false, regionTypeIndex));
    }

    private void savePlot() {
        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("saveplot", regionProtector.getBlockPos()));
    }

    private void loadPlot() {
        CCPacketHandler.INSTANCE.sendToServer(new PacketRegionProtector("loadplot", regionProtector.getBlockPos()));
    }

    @Override
    public void tick() {
        super.tick();

        regionRuleSetBtns[0].setMessage(new TranslatableComponent(getRuleButtonKey(0)));
        regionRuleSetBtns[1].setMessage(new TranslatableComponent(getRuleButtonKey(1)));
        regionRuleSetBtns[2].setMessage(new TranslatableComponent(getRuleButtonKey(2)));

        regionRuleSetBtns[3].setMessage(new TranslatableComponent(getRuleButtonKey(3)));
        regionRuleSetBtns[4].setMessage(new TranslatableComponent(getRuleButtonKey(4)));
        regionRuleSetBtns[5].setMessage(new TranslatableComponent(getRuleButtonKey(5)));

        globalBtn.setMessage(new TranslatableComponent(getGlobalButtonKey()));

        regionOffsetXBox.tick();
        regionOffsetYBox.tick();
        regionOffsetZBox.tick();

        regionSizeXBox.tick();
        regionSizeYBox.tick();
        regionSizeZBox.tick();

        priorityBox.tick();

        regionTypeBtn.setMessage(new TranslatableComponent(getRegionTypeButtonKey()));
    }

    @Override
    protected void drawGuiBackground(PoseStack poseStack, int mouseX, int mouseY) {}

    @Override
    protected void drawGuiForeground(PoseStack poseStack, int mouseX, int mouseY) {

        int editBoxYOffset = 11;
        int buttonOffset = 4;

        if (!regionProtector.profile.isGlobal()) {

            regionOffsetXBox.render(poseStack, mouseX, mouseY, 0);
            regionOffsetYBox.render(poseStack, mouseX, mouseY, 0);
            regionOffsetZBox.render(poseStack, mouseX, mouseY, 0);

            regionSizeXBox.render(poseStack, mouseX, mouseY, 0);
            regionSizeYBox.render(poseStack, mouseX, mouseY, 0);
            regionSizeZBox.render(poseStack, mouseX, mouseY, 0);

            TranslatableComponent regionOffsetText = new TranslatableComponent("screen.regionprotector.txt.regionoffset");
            minecraft.font.draw(poseStack, regionOffsetText, regionOffsetXBox.x, regionOffsetXBox.y - editBoxYOffset, 0xFFFFFF);

            TranslatableComponent regionEdgeText = new TranslatableComponent("screen.regionprotector.txt.regionsize");
            minecraft.font.draw(poseStack, regionEdgeText, regionSizeXBox.x, regionSizeXBox.y - editBoxYOffset, 0xFFFFFF);
        }

        priorityBox.render(poseStack, mouseX, mouseY, 0);

        TranslatableComponent priorityText = new TranslatableComponent("screen.regionprotector.txt.priority");
        minecraft.font.draw(poseStack, priorityText, priorityBox.x, priorityBox.y - editBoxYOffset, 0xFFFFFF);

        TranslatableComponent globalText = new TranslatableComponent("screen.regionprotector.txt.global");
        minecraft.font.draw(poseStack, globalText, globalBtn.x, globalBtn.y - editBoxYOffset, 0xFFFFFF);

        TranslatableComponent blockBreakingText = new TranslatableComponent("screen.regionprotector.txt.rule.blockbreaking");
        minecraft.font.draw(poseStack, blockBreakingText, regionRuleSetBtns[0].x - minecraft.font.width(blockBreakingText) - buttonOffset, regionRuleSetBtns[0].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent blockPlacingText = new TranslatableComponent("screen.regionprotector.txt.rule.blockplacing");
        minecraft.font.draw(poseStack, blockPlacingText, regionRuleSetBtns[1].x - minecraft.font.width(blockPlacingText) - buttonOffset, regionRuleSetBtns[1].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent blockUsingText = new TranslatableComponent("screen.regionprotector.txt.rule.blockusing");
        minecraft.font.draw(poseStack, blockUsingText, regionRuleSetBtns[2].x - minecraft.font.width(blockUsingText) - buttonOffset, regionRuleSetBtns[2].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent entityHurtingText = new TranslatableComponent("screen.regionprotector.txt.rule.entityhurting");
        minecraft.font.draw(poseStack, entityHurtingText, regionRuleSetBtns[3].x - minecraft.font.width(entityHurtingText) - buttonOffset, regionRuleSetBtns[3].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent entityInteractingText = new TranslatableComponent("screen.regionprotector.txt.rule.entityinteracting");
        minecraft.font.draw(poseStack, entityInteractingText, regionRuleSetBtns[4].x - minecraft.font.width(entityInteractingText) - buttonOffset, regionRuleSetBtns[4].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent pvpTxt = new TranslatableComponent("screen.regionprotector.txt.rule.pvp");
        minecraft.font.draw(poseStack, pvpTxt, regionRuleSetBtns[5].x - minecraft.font.width(pvpTxt) - buttonOffset, regionRuleSetBtns[5].y + buttonOffset, 0xFFFFFF);

        TranslatableComponent regionTxt = new TranslatableComponent("screen.regionprotector.txt.regiontype");
        minecraft.font.draw(poseStack, regionTxt, regionTypeBtn.x - minecraft.font.width(regionTxt) - buttonOffset, regionTypeBtn.y + buttonOffset, 0xFFFFFF);

        if (regionProtector.lastSaveTime != 0) {
            Date date = new Date(regionProtector.lastSaveTime);
            TextComponent lastSaveText = new TextComponent("Last Save: " + date.toString());
            minecraft.font.draw(poseStack, lastSaveText, getScreenX() - (minecraft.font.width(lastSaveText) / 2), getScreenY() + 85, 0xFFFFFF);
        }
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
