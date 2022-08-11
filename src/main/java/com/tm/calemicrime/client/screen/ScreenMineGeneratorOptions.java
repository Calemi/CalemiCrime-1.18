package com.tm.calemicrime.client.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.screen.ScreenBase;
import com.tm.calemicore.util.screen.widget.SmoothButton;
import com.tm.calemicrime.blockentity.BlockEntityMineGenerator;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.packet.PacketMineGenerator;
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
public class ScreenMineGeneratorOptions extends ScreenBase {

    private final BlockEntityMineGenerator mineGenerator;

    private EditBox regionOffsetXBox;
    private EditBox regionOffsetYBox;
    private EditBox regionOffsetZBox;

    private EditBox regionSizeXBox;
    private EditBox regionSizeYBox;
    private EditBox regionSizeZBox;

    public ScreenMineGeneratorOptions(Player player, InteractionHand hand, BlockEntityMineGenerator mineGenerator) {
        super(player, hand);
        this.mineGenerator = mineGenerator;
    }

    @Override
    protected void init() {
        super.init();

        int editBoxXOffset = -45;
        int editBoxYOffset = -12;
        int editBoxXSpace = 45;
        int editBoxYSpace = 30;

        regionOffsetXBox = initField(mineGenerator.getRegionOffset().x, editBoxXOffset, editBoxYOffset);
        regionOffsetYBox = initField(mineGenerator.getRegionOffset().y, editBoxXOffset + editBoxXSpace, editBoxYOffset);
        regionOffsetZBox = initField(mineGenerator.getRegionOffset().z, editBoxXOffset + editBoxXSpace * 2, editBoxYOffset);

        regionSizeXBox = initField(mineGenerator.getRegionSize().x, editBoxXOffset, editBoxYOffset + editBoxYSpace);
        regionSizeYBox = initField(mineGenerator.getRegionSize().y, editBoxXOffset + editBoxXSpace, editBoxYOffset + editBoxYSpace);
        regionSizeZBox = initField(mineGenerator.getRegionSize().z, editBoxXOffset + editBoxXSpace * 2, editBoxYOffset + editBoxYSpace);
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

    private void confirmEditBoxes() {

        int offsetX = parseInteger(regionOffsetXBox.getValue());
        int offsetY = parseInteger(regionOffsetYBox.getValue());
        int offsetZ = parseInteger(regionOffsetZBox.getValue());

        int edgeX = parseInteger(regionSizeXBox.getValue());
        int edgeY = parseInteger(regionSizeYBox.getValue());
        int edgeZ = parseInteger(regionSizeZBox.getValue());


        regionOffsetXBox.setValue("" + offsetX);
        regionOffsetYBox.setValue("" + offsetY);
        regionOffsetZBox.setValue("" + offsetZ);

        regionSizeXBox.setValue("" + edgeX);
        regionSizeYBox.setValue("" + edgeY);
        regionSizeZBox.setValue("" + edgeZ);

        BlockPos offset = new BlockPos(offsetX, offsetY, offsetZ);
        BlockPos edge = new BlockPos(edgeX, edgeY, edgeZ);

        CCPacketHandler.INSTANCE.sendToServer(new PacketMineGenerator("syncregion", mineGenerator.getBlockPos(), offset, edge));
    }

    private int parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException numberformatexception) {
            return 0;
        }
    }

    @Override
    public void tick() {
        super.tick();

        regionOffsetXBox.tick();
        regionOffsetYBox.tick();
        regionOffsetZBox.tick();

        regionSizeXBox.tick();
        regionSizeYBox.tick();
        regionSizeZBox.tick();
    }

    @Override
    protected void drawGuiBackground(PoseStack poseStack, int mouseX, int mouseY) {}

    @Override
    protected void drawGuiForeground(PoseStack poseStack, int mouseX, int mouseY) {

        regionOffsetXBox.render(poseStack, mouseX, mouseY, 0);
        regionOffsetYBox.render(poseStack, mouseX, mouseY, 0);
        regionOffsetZBox.render(poseStack, mouseX, mouseY, 0);

        regionSizeXBox.render(poseStack, mouseX, mouseY, 0);
        regionSizeYBox.render(poseStack, mouseX, mouseY, 0);
        regionSizeZBox.render(poseStack, mouseX, mouseY, 0);

        int editBoxYOffset = 11;

        TranslatableComponent regionOffsetText = new TranslatableComponent("screen.regionprotector.txt.regionoffset");
        minecraft.font.draw(poseStack, regionOffsetText, regionOffsetXBox.x, regionOffsetXBox.y - editBoxYOffset, 0xFFFFFF);

        TranslatableComponent regionEdgeText = new TranslatableComponent("screen.regionprotector.txt.regionsize");
        minecraft.font.draw(poseStack, regionEdgeText, regionSizeXBox.x, regionSizeXBox.y - editBoxYOffset, 0xFFFFFF);
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
