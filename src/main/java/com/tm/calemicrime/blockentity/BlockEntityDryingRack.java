package com.tm.calemicrime.blockentity;

import com.tm.calemicore.util.blockentity.BlockEntityBase;
import com.tm.calemicrime.file.DryingRackRecipesFile;
import com.tm.calemicrime.init.InitBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BlockEntityDryingRack extends BlockEntityBase {

    public ItemStack dryingStack = ItemStack.EMPTY;

    public int dryingTicks;

    public BlockEntityDryingRack(BlockPos pos, BlockState state) {
        super(InitBlockEntityTypes.DRYING_RACK.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntityDryingRack rack) {

        if (level.isClientSide()) {
            return;
        }

        if (rack.dryingStack.isEmpty()) {
            rack.dryingTicks = 0;
            return;
        }

        DryingRackRecipesFile.DryingRackRecipe recipe = DryingRackRecipesFile.getRecipe(rack.dryingStack);

        if (recipe != null) {
            rack.dryingTicks++;

            if (rack.dryingTicks >= recipe.getTicks()) {
                rack.dryingStack = recipe.getResult();
                rack.dryingTicks = 0;
                rack.markUpdated();
            }
        }

        else {
            rack.dryingTicks = 0;
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);

        if (tag.contains("DryingStack")) {
            CompoundTag stackTag = tag.getCompound("DryingStack");
            dryingStack = ItemStack.of(stackTag);
        }

        dryingTicks = tag.getInt("DryingTicks");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag stackTag = new CompoundTag();
        dryingStack.save(stackTag);
        tag.put("DryingStack", stackTag);
        tag.putInt("DryingTicks", dryingTicks);
    }
}
