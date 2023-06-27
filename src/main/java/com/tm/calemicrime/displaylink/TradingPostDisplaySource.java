package com.tm.calemicrime.displaylink;

import com.google.common.collect.ImmutableList;
import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.source.DisplaySource;
import com.simibubi.create.content.redstone.displayLink.target.DisplayTargetStats;
import com.simibubi.create.foundation.gui.ModularGuiLineBuilder;
import com.simibubi.create.foundation.utility.Lang;
import com.tm.calemieconomy.blockentity.BlockEntityTradingPost;
import com.tm.calemieconomy.util.helper.CurrencyHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

public class TradingPostDisplaySource  extends DisplaySource {

    public TradingPostDisplaySource() {}

    @Override
    public List<MutableComponent> provideText(DisplayLinkContext displayLinkContext, DisplayTargetStats displayTargetStats) {

        List<MutableComponent> text = new ArrayList<>();

        if (displayLinkContext.getSourceBlockEntity() instanceof BlockEntityTradingPost post) {

            String tradeType = post.buyMode ? "Buying: " : "Selling: ";
            text.add(new TextComponent(tradeType + post.tradeAmount + "x " + post.getStackForSale().getDisplayName().getString()));
            text.add(new TextComponent("Price: " + CurrencyHelper.formatCurrency(post.tradePrice, true).getString()));

            String locationLabel = displayLinkContext.sourceConfig().getString("Label");
            if (locationLabel.isEmpty()) {
                text.add(new TextComponent("Location: " + post.getLocation().toString()));
            }

            else {
                text.add(new TextComponent("Location: [" + locationLabel + "]"));
            }
        }

        return text;
    }

    @OnlyIn(Dist.CLIENT)
    public void initConfigurationWidgets(DisplayLinkContext context, ModularGuiLineBuilder builder, boolean isFirstLine) {
        if (isFirstLine) {
            this.addLabelingTextBox(builder);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected void addLabelingTextBox(ModularGuiLineBuilder builder) {
        builder.addTextInput(0, 137, (e, t) -> {
            e.setValue("");
            t.withTooltip(ImmutableList.of(Lang.translateDirect("display_source.label", new Object[0]).withStyle((s) -> {
                return s.withColor(5476833);
            }), Lang.translateDirect("gui.schedule.lmb_edit", new Object[0]).withStyle(new ChatFormatting[]{ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC})));
        }, "Label");
    }
}

