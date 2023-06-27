package com.tm.calemicrime.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import com.tm.calemicrime.blockentity.BlockEntityRadiationProjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderRadiationProjector implements BlockEntityRenderer<BlockEntityRadiationProjector> {

    public RenderRadiationProjector(BlockEntityRendererProvider.Context pContext) {}

    @Override
    public void render(BlockEntityRadiationProjector radiationProjector, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {

        LocalPlayer player = Minecraft.getInstance().player;

        if (player.isCreative() && Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {

            if (radiationProjector != null && radiationProjector.getRegionOffset() != null && radiationProjector.getRegionSize() != null) {
                VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.lines());
                LevelRenderer.renderLineBox(poseStack, vertexconsumer,
                        radiationProjector.getRegionOffset().x, radiationProjector.getRegionOffset().y, radiationProjector.getRegionOffset().z,
                        radiationProjector.getRegionOffset().x + radiationProjector.getRegionSize().x, radiationProjector.getRegionOffset().y + radiationProjector.getRegionSize().y, radiationProjector.getRegionOffset().z + radiationProjector.getRegionSize().z,
                        0, 0.75F, 0F, 1F);
            }
        }

        if (Minecraft.getInstance().isPaused()) {
            return;
        }

        for (int i = 0; i < radiationProjector.getRadiationStrength(); i++) {
            renderRadiation(player, radiationProjector);
        }
    }

    private void renderRadiation(LocalPlayer player, BlockEntityRadiationProjector radiationProjector) {
        float radius = 10;

        double randX = player.getX() + ((player.getLevel().random.nextFloat() * radius) - (radius / 2));
        double randY = player.getY() + ((player.getLevel().random.nextFloat() * radius) - (radius / 2));
        double randZ = player.getZ() + ((player.getLevel().random.nextFloat() * radius) - (radius / 2));

        if (radiationProjector.getRegion().contains(randX, randY, randZ)) {
            player.getLevel().addParticle(new DustParticleOptions(new Vector3f(0, 1, 0), 1.0F), randX, randY, randZ, 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    public boolean shouldRender(BlockEntityRadiationProjector radiationProjector, Vec3 cameraPos) {
        return true;
    }

    @Override
    public boolean shouldRenderOffScreen(BlockEntityRadiationProjector radiationProjector) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 96;
    }
}
