package com.tm.calemicrime.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.tm.calemicrime.blockentity.BlockEntityRegionProtector;
import com.tm.calemicrime.util.RegionProfile;
import com.tm.calemicrime.util.RegionRuleSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderRegionProtector implements BlockEntityRenderer<BlockEntityRegionProtector> {

    public RenderRegionProtector(BlockEntityRendererProvider.Context pContext) {}

    @Override
    public void render(BlockEntityRegionProtector regionProtector, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (mc.getEntityRenderDispatcher().shouldRenderHitBoxes()) {

            if (regionProtector != null && regionProtector.profile.getOffset() != null && regionProtector.profile.getSize() != null) {

                if (regionProtector.profile.isGlobal()) {
                    return;
                }

                Vec3 color = new Vec3(1, 1, 1);

                if (regionProtector.profile.getRuleSet().ruleSets[5] == RegionRuleSet.RuleOverrideType.ALLOW) {
                    color = new Vec3(1, 0, 0);
                }

                else if (regionProtector.profile.getType() == RegionProfile.Type.RESIDENTIAL) {
                    color = new Vec3(0, 1, 0);
                }

                else if (regionProtector.profile.getType() == RegionProfile.Type.COMMERCIAL) {
                    color = new Vec3(0, 0, 1);
                }

                if (regionProtector.profile.getRentAcceptor() != null && regionProtector.profile.getRentAcceptor().getRemainingRentSeconds() > 0) {
                    color = color.multiply(0.4F, 0.4F, 0.4F);
                }

                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.lines());

                if (regionProtector.profile.getType() != RegionProfile.Type.NONE || player.isCreative() || player.isSpectator()) {
                    LevelRenderer.renderLineBox(poseStack, vertexconsumer,
                            regionProtector.profile.getOffset().x, regionProtector.profile.getOffset().y, regionProtector.profile.getOffset().z,
                            regionProtector.profile.getOffset().x + regionProtector.profile.getSize().x, regionProtector.profile.getOffset().y + regionProtector.profile.getSize().y, regionProtector.profile.getOffset().z + regionProtector.profile.getSize().z,
                            (float)color.x, (float)color.y, (float)color.z, 1F);
                }

                if (player.isSpectator()) {
                    LevelRenderer.renderLineBox(poseStack, vertexconsumer,
                            0, 0, 0, 1, 1, 1,
                            (float)color.x, (float)color.y, (float)color.z, 1F);
                }
            }
        }
    }

    @Override
    public boolean shouldRender(BlockEntityRegionProtector regionProtector, Vec3 cameraPos) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntityRegionProtector regionProtector) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 5000;
    }
}
