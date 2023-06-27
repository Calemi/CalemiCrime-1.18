package com.tm.calemicrime.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tm.calemicore.util.helper.LogHelper;
import com.tm.calemicore.util.helper.ScreenHelper;
import com.tm.calemicore.util.helper.SoundHelper;
import com.tm.calemicore.util.screen.ScreenBase;
import com.tm.calemicrime.main.CCReference;
import com.tm.calemicrime.packet.CCPacketHandler;
import com.tm.calemicrime.packet.PacketGenerateLoot;
import com.tm.calemicrime.packet.PacketGiveLootReward;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ScreenLootBox extends ScreenBase {

    public ItemStack[] stacks;
    public int[] rarities;

    private int lastIndex;

    private double time = 0;

    public ScreenLootBox(Player player, String pool) {
        super(player, InteractionHand.MAIN_HAND);
        CCPacketHandler.INSTANCE.sendToServer(new PacketGenerateLoot(pool));
    }

    @Override
    protected void init() {
        super.init();
        SoundHelper.playAtPlayer(player, SoundEvents.ENDER_CHEST_OPEN, 1, 1);
    }

    @Override
    public void tick() {
        super.tick();
        time++;
    }

    @Override
    protected void drawGuiBackground(PoseStack poseStack, int mouseX, int mouseY) {}

    @Override
    protected void drawGuiForeground(PoseStack poseStack, int mouseX, int mouseY) {

        if (stacks == null || rarities == null) {
            return;
        }

        double offset = (Math.pow(1.04F, -time) * 700) - 704;

        for (int i = 0; i < stacks.length; i++) {

            double x = (getScreenX() + i * 64) + offset;

            ItemStack visual = stacks[i].copy();

            LogHelper.log(CCReference.MOD_NAME, stacks[i]);

            TextComponent component = new TextComponent("x" + visual.getCount());

            if (stacks[i].getCount() > 1) {
                poseStack.pushPose();
                poseStack.translate(x + 20, getScreenY() + 20, 150);
                drawCenteredString(poseStack, Minecraft.getInstance().font, component, 0, 0, 0xFFFFFF);
                poseStack.popPose();
            }

            visual.setCount(1);
            renderItem(visual, x, getScreenY());
        }

        int index = (int)(-offset + 32) / 64;

        if (index >= stacks.length) {
            index = 0;
        }

        ItemStack currentStack = stacks[index];

        String rewardString = switch (rarities[index]) {
            case (0) -> "Common";
            case (1) -> ChatFormatting.GREEN + "Uncommon";
            case (2) -> ChatFormatting.BLUE + "Rare";
            case (3) -> ChatFormatting.RED + "Super Rare";
            case (4) -> ChatFormatting.GOLD + "" + ChatFormatting.BOLD + "Legendary";
            default -> "";
        };

        rewardString += " Reward";

        ScreenHelper.drawCenteredString(poseStack, getScreenX(), getScreenY() + 40, 0, 0xFFFFFF, new TextComponent(rewardString));

        List<Component> list = currentStack.getTooltipLines(player, TooltipFlag.Default.NORMAL);

        for (int i = 0; i < list.size(); i++) {
            ScreenHelper.drawCenteredString(poseStack, getScreenX(), getScreenY() + 50 + (i * 10), 0, 0xFFFFFF, new TextComponent("").append(list.get(i)));
        }

        if (index != lastIndex) {
            lastIndex = index;
            SoundHelper.playAtPlayer(player, SoundEvents.UI_BUTTON_CLICK, 1, 1);
        }

        if (time >= 140) {
            CCPacketHandler.INSTANCE.sendToServer(new PacketGiveLootReward(currentStack));
            Minecraft.getInstance().setScreen(null);
            SoundHelper.playAtPlayer(player, SoundEvents.PLAYER_LEVELUP, 1, 1);
        }
    }

    protected void renderItem(ItemStack stack, double x, double y) {
        BakedModel bakedModel = Minecraft.getInstance().getItemRenderer().getModel(stack, (Level)null, (LivingEntity)null, 0);
        Minecraft.getInstance().textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
        RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        PoseStack posestack = RenderSystem.getModelViewStack();
        posestack.pushPose();
        posestack.translate(x, y, (100.0F + Minecraft.getInstance().getItemRenderer().blitOffset));
        posestack.scale(1.0F, -1.0F, 1.0F);
        posestack.scale(64.0F, 64.0F, 64.0F);
        RenderSystem.applyModelViewMatrix();
        PoseStack posestack1 = new PoseStack();
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        boolean flag = !bakedModel.usesBlockLight();
        if (flag) {
            Lighting.setupForFlatItems();
        }

        Minecraft.getInstance().getItemRenderer().render(stack, ItemTransforms.TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
        multibuffersource$buffersource.endBatch();
        RenderSystem.enableDepthTest();
        if (flag) {
            Lighting.setupFor3DItems();
        }

        posestack.popPose();
        RenderSystem.applyModelViewMatrix();
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
    public boolean shouldCloseOnEsc() {
        return player.isCreative();
    }

    @Override
    protected boolean canCloseWithInvKey() {
        return false;
    }

    @Override
    public boolean isPauseScreen () {
        return false;
    }
}
